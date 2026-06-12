def check(m, a, k):
    b = [1 if x >= m else -1 for x in a]
    prefix = [0] * (len(a) + 1)
    for i in range(len(a)):
        prefix[i + 1] = prefix[i] + b[i]
    min_prefix = 0
    for i in range(k, len(a) + 1):
        if prefix[i] - min_prefix > 0:
            return True
        min_prefix = min(min_prefix, prefix[i - k + 1])
    return False

n, k = map(int, input().split())
a = list(map(int, input().split()))

left, right = 1, n
while left < right:
    mid = (left + right + 1) // 2
    if check(mid, a, k):
        left = mid
    else:
        right = mid - 1

print(left)