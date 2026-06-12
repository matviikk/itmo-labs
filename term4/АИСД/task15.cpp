#include <fstream>
#include <queue>
#include <vector>

using namespace std;

int main() {
    ifstream in("input.txt");
    ofstream out("output.txt");

    int n, m, k;
    in >> n >> m >> k;

    vector<vector<int>> fire_time(n, vector<int>(m, -1));
    queue<pair<int, int>> q;

    for (int i = 0; i < k; ++i) {
        int x, y;
        in >> x >> y;
        --x; --y;
        fire_time[x][y] = 0;
        q.push({x, y});
    }

    int dx[] = {-1, 1, 0, 0};
    int dy[] = {0, 0, -1, 1};

    while (!q.empty()) {
        auto [x, y] = q.front(); q.pop();
        for (int d = 0; d < 4; ++d) {
            int nx = x + dx[d], ny = y + dy[d];
            if (nx >= 0 && nx < n && ny >= 0 && ny < m && fire_time[nx][ny] == -1) {
                fire_time[nx][ny] = fire_time[x][y] + 1;
                q.push({nx, ny});
            }
        }
    }

    int max_time = -1;
    int res_x = 0, res_y = 0;

    for (int i = 0; i < n; ++i)
        for (int j = 0; j < m; ++j)
            if (fire_time[i][j] > max_time) {
                max_time = fire_time[i][j];
                res_x = i;
                res_y = j;
            }

    out << (res_x + 1) << " " << (res_y + 1) << "\n";
    return 0;
}