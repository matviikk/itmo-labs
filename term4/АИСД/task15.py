from collections import deque

def main():
    with open('input.txt', 'r') as f:
        lines = f.read().split()

    n = int(lines[0])
    m = int(lines[1])
    k = int(lines[2])

    fire_time = [[-1] * m for _ in range(n)]
    queue = deque()

    idx = 3
    for _ in range(k):
        x = int(lines[idx]) - 1
        y = int(lines[idx + 1]) - 1
        fire_time[x][y] = 0
        queue.append((x, y))
        idx += 2

    dx = (-1, 1, 0, 0)
    dy = (0, 0, -1, 1)

    while queue:
        x, y = queue.popleft()
        t = fire_time[x][y]
        for d in range(4):
            nx = x + dx[d]
            ny = y + dy[d]
            if 0 <= nx < n and 0 <= ny < m and fire_time[nx][ny] == -1:
                fire_time[nx][ny] = t + 1
                queue.append((nx, ny))

    max_time = -1
    res_x, res_y = 0, 0
    for i in range(n):
        row = fire_time[i]
        for j in range(m):
            if row[j] > max_time:
                max_time = row[j]
                res_x, res_y = i, j

    with open('output.txt', 'w') as f:
        f.write(f"{res_x + 1} {res_y + 1}\n")


main()