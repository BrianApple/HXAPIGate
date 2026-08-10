#!/usr/bin/env python3
"""HXAPIGate 日志查询页面截图：列表 + 链路抽屉（evaluate 注入，避免元素交互时序问题）"""
import asyncio, os
from playwright.async_api import async_playwright

BASE = "http://127.0.0.1:18080/static/index.html"
OUT = "/data/hermes_files/HXAPIGate/scripts/ws_shots"
os.makedirs(OUT, exist_ok=True)

async def wait_sel(page, sel, timeout=15000):
    await page.wait_for_selector(sel, timeout=timeout, state="visible")

async def shot(page, name, delay=900):
    await page.wait_for_timeout(delay)
    await page.screenshot(path=os.path.join(OUT, name))
    print(f"  ✓ {name}")

async def main():
    async with async_playwright() as p:
        browser = await p.chromium.launch(
            executable_path="/root/.cache/ms-playwright/chromium-1223/chrome-linux64/chrome"
        )
        ctx = await browser.new_context(viewport={"width": 1440, "height": 900}, device_scale_factor=2)
        page = await ctx.new_page()

        # 登录
        await page.goto(BASE, wait_until="networkidle")
        await wait_sel(page, "input[placeholder*='用户名']")
        await page.fill("input[placeholder*='用户名']", "admin")
        await page.fill("input[placeholder*='密码']", "123456")
        await page.click("button:has-text('登 录'), button:has-text('登录')")
        await page.wait_for_url("**#/welcome**", timeout=10000)
        await page.wait_for_timeout(1000)

        # 日志查询页
        await page.goto(BASE + "#/log", wait_until="networkidle")
        await page.wait_for_timeout(2500)
        await wait_sel(page, ".el-table tbody tr", timeout=15000)
        await shot(page, "log-search.png")

        # 注入 TraceId 查询（真实键盘输入，确保 Vue v-model 更新）
        await page.click("input[placeholder*='请求溯源']", timeout=8000)
        await page.keyboard.type("wstest-final", delay=30)
        await page.wait_for_timeout(400)
        await page.click("button:has-text('查询')")
        # 等待表格刷新为 2 条
        await page.wait_for_function("() => document.body.innerText.includes('日志记录（2')", timeout=10000)
        await page.wait_for_timeout(800)
        await shot(page, "log-search-trace.png")

        # 打开链路抽屉
        await page.click("button:has-text('链路')", timeout=6000)
        await wait_sel(page, ".el-drawer", timeout=6000)
        await page.wait_for_timeout(1200)
        await shot(page, "log-trace-drawer.png")

        await browser.close()
        print("ALL DONE ->", OUT)

asyncio.run(main())
