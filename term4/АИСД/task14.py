import sys

sys.setrecursionlimit(10 ** 7)

def dfs(i, j, grid, visited, n, m):
    stack = [(i, j)]
    total = 0
    while stack:
        x, y = stack.pop()
        if visited[x][y]:
            continue
        visited[x][y] = True
        total += grid[x][y]
        for dx, dy in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
            nx, ny = x + dx, y + dy
            if 0 <= nx < n and 0 <= ny < m:
                if not visited[nx][ny] and grid[nx][ny] > 0:
                    stack.append((nx, ny))
    return total


def main():
    input = sys.stdin.readline
    t = int(input())
    results = []

    for _ in range(t):
        n, m = map(int, input().split())
        grid = [list(map(int, input().split())) for _ in range(n)]
        visited = [[False] * m for _ in range(n)]
        max_volume = 0
        for i in range(n):
            for j in range(m):
                if not visited[i][j] and grid[i][j] > 0:
                    volume = dfs(i, j, grid, visited, n, m)
                    max_volume = max(max_volume, volume)
        results.append(max_volume)

    for res in results:
        print(res)


if __name__ == "__main__":
    main()