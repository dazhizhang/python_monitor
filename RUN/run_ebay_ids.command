#!/bin/bash

# 🔧 提高文件描述符限制（防止 too many open files 报错）
ulimit -n 65536

# 📂 进入项目目录
cd ~/Desktop/scarper || { echo "❌ 路径错误 /Users/o/Desktop/scarper"; exit 1; }
echo "📂 当前路径为：$(pwd)"

# 🐍 激活虚拟环境
source venv/bin/activate || { echo "❌ 虚拟环境激活失败"; exit 1; }

# ▶️ 执行抓取脚本
python scrap_ebay_ids.py -r stl -m 10

# ✅ 结束提示
echo ""
echo "✅ eBay 抓取脚本执行完毕，按任意键关闭窗口..."
read
