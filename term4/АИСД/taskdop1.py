from collections import Counter

n, I = map(int, input().split())
a = list(map(int, input().split()))

max_bits = I * 8 // n
max_distinct = 2 ** min(max_bits, 30)
count = Counter(a)
unique = sorted(count)
freq = [count[x] for x in unique]

if len(unique) <= max_distinct:
    print(0)
else:
    prefix = [0]
    for f in freq:
        prefix.append(prefix[-1] + f)

    min_change = float('inf')
    for i in range(len(unique) - max_distinct + 1):
        kept = prefix[i + max_distinct] - prefix[i]
        min_change = min(min_change, n - kept)

    print(min_change)