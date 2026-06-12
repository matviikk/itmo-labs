import sys
import heapq

def main():
    input = sys.stdin.readline

    n, d = map(int, input().split())
    raw_a = list(map(int, input().split()))
    a = [0] + raw_a + [0]

    coords = [tuple(map(int, input().split())) for _ in range(n)]

    def dist(i, j):
        return abs(coords[i][0] - coords[j][0]) + abs(coords[i][1] - coords[j][1])

    min_money = [float('inf')] * n
    hq = [(0, 0, a[0])]
    min_money[0] = 0

    while hq:
        cost, u, time = heapq.heappop(hq)
        if u == n - 1:
            print(cost)
            return
        if cost > min_money[u]:
            continue
        for v in range(n):
            if u == v:
                continue
            travel = d * dist(u, v)
            if time >= travel:
                need_money = 0
            else:
                need_money = travel - time
            new_cost = cost + need_money
            new_time = time - travel + a[v] + need_money
            if new_cost < min_money[v]:
                min_money[v] = new_cost
                heapq.heappush(hq, (new_cost, v, new_time))

if __name__ == "__main__":
    main()