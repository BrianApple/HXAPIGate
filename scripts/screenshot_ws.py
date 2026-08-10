#!/usr/bin/env python3
"""HXAPIGate WebSocket 功能前端截图：API列表(WebSocket标签) + 编辑对话框(WebSocket协议选项+节点配置)"""
import asyncio, os
from playwright.async_api import async_playwright

BASE = "http://127.0.0.1:18080/static/index.html"
OUT = "/data/hermes_files/HXAPIGate/scripts/ws_shots"
os.makedirs(OUT, exist_ok=True)

async def wait_sel(page, sel, timeout=8000):
    await page.wait_for_selector(sel, timeout=timeout, state="visible")

async def shot(page, name, selector=None, delay=700):
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
        ctx = await browser.new_context(viewport={"width": 1440, "height": 900}, device_scale_factor=2)
        page = await ctx.new_page()

        # 1. 登录
        await page.goto(BASE, wait_until="networkidle")
        await wait_sel(page, "input[placeholder*='用户名']")
        await page.fill("input[placeholder*='用户名']", "admin")
        await page.fill("input[placeholder*='密码']", "123456")
        await page.click("button:has-text('登 录'), button:has-text('登录')")
        await page.wait_for_url("**#/welcome**", timeout=8000)

        # 2. API 列表页
        await page.goto(BASE + "#/api", wait_until="networkidle")
        await wait_sel(page, ".el-table", timeout=8000)
        await page.wait_for_timeout(1200)

        # 找 /ws/echo 行（可能在第二页，列表按 id 升序）
        row = page.locator("tr", has_text="/ws/echo")
        if await row.count() == 0:
            # 翻到第二页
            await page.click(".el-pagination .el-pager li:has-text('2')", timeout=5000)
            await page.wait_for_timeout(1200)
            row = page.locator("tr", has_text="/ws/echo")
        print("  /ws/echo 行数:", await row.count())
        await shot(page, "api-list-ws.png", selector=".el-table")

        # 3. 点击该行「编辑」打开对话框
        await row.locator("button:has-text('编辑')").first.click(timeout=5000)
        await wait_sel(page, ".el-dialog", timeout=5000)
        await page.wait_for_timeout(1000)

        # 3a. 对话框上部：协议选项区（WebSocket 选中）
        await shot(page, "ws-edit-protocol.png", selector=".el-dialog")

        # 3b. 滚轮滚动对话框，截取底部（后端节点+熔断+保存按钮）
        await page.mouse.move(720, 400)
        await page.mouse.wheel(0, 700)
        await page.wait_for_timeout(700)
        await shot(page, "ws-edit-node.png", selector=".el-dialog")

        await browser.close()
        print("ALL DONE ->", OUT)

asyncio.run(main())
