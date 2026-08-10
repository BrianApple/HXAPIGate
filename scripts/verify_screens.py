#!/usr/bin/env python3
"""验证页面真实渲染数据并重截关键图"""
import asyncio, os
from playwright.async_api import async_playwright

BASE = "http://127.0.0.1:18080/static/index.html"
OUT = "/data/hermes_files/HXAPIGate/HXBootShiro/src/main/resources/static/images"

async def main():
    async with async_playwright() as p:
        browser = await p.chromium.launch(
            executable_path="/root/.cache/ms-playwright/chromium-1223/chrome-linux64/chrome"
        )
        ctx = await browser.new_context(viewport={"width": 1440, "height": 900}, device_scale_factor=2)
        page = await ctx.new_page()
        await page.goto(BASE, wait_until="networkidle")
        await page.fill("input[placeholder*='用户名']", "admin")
        await page.fill("input[placeholder*='密码']", "123456")
        await page.click("button:has-text('登 录'), button:has-text('登录')")
        await page.wait_for_url("**#/welcome**", timeout=8000)
        await page.wait_for_timeout(1500)

        # 首页统计卡片文本
        stats = await page.eval_on_selector_all(".el-statistic__number, .el-card .el-statistic, .el-statistic",
            "els => els.map(e => e.innerText.trim())")
        print("首页统计:", stats)
        body = await page.evaluate("document.body.innerText.slice(0, 300)")
        print("首页文本:", body.replace(chr(10), ' | '))
        await page.screenshot(path=os.path.join(OUT, "index.png"))
        print("✓ index.png 重截")

        # 类型页
        await page.goto(BASE + "#/apiType", wait_until="networkidle")
        await page.wait_for_timeout(1200)
        print("类型页:", (await page.evaluate("document.body.innerText.slice(0,200)")).replace(chr(10),' | '))

        # API 页行数
        await page.goto(BASE + "#/api", wait_until="networkidle")
        await page.wait_for_timeout(1200)
        rows = await page.eval_on_selector_all(".el-table__body tbody tr", "els => els.length")
        print("API 表格行数:", rows)

        # 授权页
        await page.goto(BASE + "#/roleAuth/1", wait_until="networkidle")
        await page.wait_for_timeout(1200)
        print("授权页:", (await page.evaluate("document.body.innerText.slice(0,250)")).replace(chr(10),' | '))
        await browser.close()

asyncio.run(main())
