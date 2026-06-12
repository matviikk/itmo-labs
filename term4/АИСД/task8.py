n = int(input())
a = list(map(int, input().split()))
b = list(map(int, input().split()))

swaps = []

for i in range(n):
    while b[i] != a[i]:
        for j in range(i, n):
            if b[j] == a[i]:
                break
        for k in range(j, i, -1):
            b[k], b[k-1] = b[k-1], b[k]
            swaps.append((k, k + 1))

print(len(swaps))
for swap in swaps:
    print(swap[0], swap[1])