string = str(input())
X = []
for i in range(7):
    X.append(int(string[i]))
r1, r2, i1, r3, i2, i3, i4 = X[0], X[1], X[2], X[3], X[4], X[5], X[6]

s1 = str(r1 ^ i1 ^ i2 ^ i4)
s2 = str(r2 ^ i1 ^ i3 ^ i4)
s3 = str(r3 ^ i2 ^ i3 ^ i4)

S = ''
S = s1 + s2 + s3

if S == '000':
    print('Ошибок нет')
if S == '001':
    r3 = 1 - r3
    print('Ошибочный бит: 4')
if S == '010':
    r2 = 1 - r2
    print('Ошибочный бит: 2')
if S == '011':
    i3 = 1 - i3
    print('Ошибочный бит: 6')
if S == '100':
    r1 = 1 - r1
    print('Ошибочный бит: 1')
if S == '101':
    i2 = 1 - i2
    print('Ошибочный бит: 5')
if S == '110':
    i1 = 1 - i1
    print('Ошибочный бит: 3')
if S == '111':
    i4 = 1 - i4
    print('Ошибочный бит: 7')
new_string = str(r1) + str(r2) + str(i1) + str(r3) + str(i2) + str(i3) + str(i4)
print('Исправленная последовательность:', new_string)