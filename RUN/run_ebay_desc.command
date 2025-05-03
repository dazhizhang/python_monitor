#!/bin/bash
# 🔧 提高文件描述符限制（防止 too many open files 报错）
ulimit -n 65536
cd ~/Desktop/scarper || { echo "❌ 路径错误 /Users/o/Desktop/scarper"; exit 1; }
echo "📂 当前路径为：$(pwd)"
source venv/bin/activate || { echo "❌ 虚拟环境激活失败"; exit 1; }
python ebay_scrap_desc.py
echo ""
echo "✅ eBay 抓取脚本执行完毕，按任意键关闭窗口..."
read
