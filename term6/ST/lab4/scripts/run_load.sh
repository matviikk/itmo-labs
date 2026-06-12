#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

target="${1:-all}"
configs=()
case "$target" in
  all)     configs=(1 2 3) ;;
  config1) configs=(1) ;;
  config2) configs=(2) ;;
  config3) configs=(3) ;;
  *) echo "использование: $0 [all|config1|config2|config3]" >&2; exit 2 ;;
esac

HOST="${HOST:-stload.se.ifmo.ru}"
PORT="${PORT:-8080}"

mkdir -p results

if ! command -v jmeter >/dev/null 2>&1; then
  echo "jmeter не найден. Установить: brew install jmeter" >&2
  exit 1
fi

if ! /usr/bin/nc -z -G 3 "$HOST" "$PORT" 2>/dev/null; then
  echo "ВНИМАНИЕ: ${HOST}:${PORT} не отвечает — проверь SSH-туннель / VPN" >&2
fi

echo "==> Цель: http://${HOST}:${PORT}"

for c in "${configs[@]}"; do
  jmx="load/load_config${c}.jmx"
  jtl="results/load_config${c}.jtl"
  log="results/load_config${c}.log"
  rm -f "$jtl" "$log"
  echo "==> [load] config $c → $jtl"
  jmeter -n -t "$jmx" -l "$jtl" -j "$log" \
    -JHOST="$HOST" -JPORT="$PORT" \
    -Jjmeter.save.saveservice.output_format=csv \
    -Jjmeter.save.saveservice.print_field_names=true
  echo "    готово — $jtl ($(wc -l < "$jtl") строк)"
done

echo
echo "==> Сводный отчёт (время отклика в мс, счётчики статусов):"
python3 scripts/analyze.py results/load_config*.jtl --mode load
