def read_line():
    while True:
        line = input().strip()
        if line:
            return line


t = int(read_line())

for _ in range(t):
    n, m = map(int, read_line().split())

    points = []
    for i in range(m):
        x, w = map(int, read_line().split())
        points.append((w, x, i + 1))

    points.sort()
    selected = points[:2 * n]

    total_weight = sum(p[0] for p in selected)
    print(total_weight)

    selected.sort(key=lambda p: p[1])

    for k in range(n):
        left = selected[k]
        right = selected[2 * n - 1 - k]
        left_index = left[2]
        right_index = right[2]

        if left_index < right_index:
            print(left_index, right_index)
        else:
            print(right_index, left_index)