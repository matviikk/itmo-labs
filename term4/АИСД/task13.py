import sys
import heapq

def dijkstra(n, graph):
    dist = [float('inf')] * (n + 1)
    parent = [-1] * (n + 1)
    dist[1] = 0
    pq = [(0, 1)]

    while pq:
        d, u = heapq.heappop(pq)
        if d > dist[u]:
            continue
        for v, w in graph[u]:
            if dist[v] > dist[u] + w:
                dist[v] = dist[u] + w
                parent[v] = u
                heapq.heappush(pq, (dist[v], v))

    return dist, parent

def restore_path(parent, n):
    path = []
    current = n
    while current != -1:
        path.append(current)
        current = parent[current]
    path.reverse()
    return path

def main():
    input = sys.stdin.readline
    n, m = map(int, input().split())
    graph = [[] for _ in range(n + 1)]
    for _ in range(m):
        a, b, w = map(int, input().split())
        graph[a].append((b, w))
        graph[b].append((a, w))

    dist, parent = dijkstra(n, graph)

    if dist[n] == float('inf'):
        print(-1)
    else:
        print(*restore_path(parent, n))

if __name__ == "__main__":
    main()