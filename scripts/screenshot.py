#!/usr/bin/env python3
"""HXAPIGate README 截图脚本：登录 → 各页面截图（1440x900 视口 + DPR2）"""
import asyncio, os
from playwright.async_api import async_playwright

BASE = "http://127.0.0.1:18080/static/index.html"
OUT = "/data/hermes_files/HXAPIGate/HXBootShiro/src/main/resources/static/images"
os.makedirs(OUT, exist_ok=True)

async def wait_sel(page, sel, timeout=8000):
    await page.wait_for_selector(sel, timeout=timeout, state="visible")

async def shot(page, name, selector=None, delay=600):
    if selector:
        try:
            await wait_sel(page, selector)
        except Exception as e:
            print(f"  ! {name}: selector {selector} not found: {e}")
    await page.wait_for_timeout(delay)
    await page.screenshot(path=os.path.join(OUT, name))
    print(f"  ✓ {name}")

async def main():
    async with async_playwright() as p:
        browser = await p.chromium.launch(
            executable_path="/root/.cache/ms-playwright/chromium-1223/chrome-linux64/chrome"
        )
        ctx = await browser.new_context(
            viewport={"width": 1440, "height": 900},
            device_scale_factor=2,
        )
        page = await ctx.new_page()

        # 1. 登录页
        await page.goto(BASE, wait_until="networkidle")
        await wait_sel(page, "input[placeholder*='用户名']")
        await page.fill("input[placeholder*='用户名']", "admin")
        await page.fill("input[placeholder*='密码']", "123456")
        await shot(page, "login.png", delay=800)

        # 2. 登录进入首页
        await page.click("button:has-text('登 录'), button:has-text('登录')")
        await page.wait_for_url("**#/welcome**", timeout=8000)
        await wait_sel(page, ".el-card, .el-statistic, .el-col", timeout=8000)
        await shot(page, "index.png", delay=1000)

        # 3. 类型页
        await page.goto(BASE + "#/apiType", wait_until="networkidle")
        await wait_sel(page, ".el-table, .el-form", timeout=8000)
        await shot(page, "type.png", delay=1000)

        # 4. API 列表页
        await page.goto(BASE + "#/api", wait_until="networkidle")
        await wait_sel(page, ".el-table", timeout=8000)
        await shot(page, "api.png", delay=1000)

        # 5. 新增 API 对话框
        await page.click("button:has-text('新增')", timeout=5000)
        await wait_sel(page, ".el-dialog", timeout=5000)
        await shot(page, "addApi.png", delay=800)
        await page.keyboard.press("Escape")

        # 6. 角色列表页
        await page.goto(BASE + "#/role", wait_until="networkidle")
        await wait_sel(page, ".el-table", timeout=8000)
        await shot(page, "role.png", delay=1000)

        # 7. 角色授权页（roleAuth/1）
        await page.goto(BASE + "#/roleAuth/1", wait_until="networkidle")
        await wait_sel(page, ".el-transfer, .el-card, .el-table", timeout=8000)
        await shot(page, "auth.png", delay=1000)

        await browser.close()
        print("ALL DONE ->", OUT)

asyncio.run(main())
