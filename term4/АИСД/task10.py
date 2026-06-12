n = int(input())
rounds = []
scores = {}

for _ in range(n):
    name, score = input().split()
    score = int(score)
    rounds.append((name, score))
    scores[name] = scores.get(name, 0) + score

max_score = max(scores.values())
candidates = {name for name, score in scores.items() if score == max_score}

current_scores = {}
for name, score in rounds:
    current_scores[name] = current_scores.get(name, 0) + score
    if current_scores[name] >= max_score and name in candidates:
        print(name)
        break