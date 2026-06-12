set -euo pipefail

cd "$(dirname "$0")/.."

config="${1:-3}"

case "$config" in
  1|2|3) ;;
  *) echo "использование: $0 [1|2|3]" >&2; exit 2 ;;
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

jtl="results/stress_config${config}.jtl"
log="results/stress_config${config}.log"
rm -f "$jtl" "$log"

echo "==> Цель: http://${HOST}:${PORT}  (config=$config)"
echo "==> [stress] config $config → $jtl"
jmeter -n -t stress/stress.jmx -l "$jtl" -j "$log" \
  -JHOST="$HOST" -JPORT="$PORT" -JCONFIG="$config" \
  -Jjmeter.save.saveservice.output_format=csv \
  -Jjmeter.save.saveservice.print_field_names=true

echo "    готово — $jtl ($(wc -l < "$jtl") строк)"
echo
python3 scripts/analyze.py "$jtl" --mode stress
