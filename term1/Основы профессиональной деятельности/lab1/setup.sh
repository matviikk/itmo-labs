#1
mkdir course1/sem1/csbasic/lab0
cd course1/sem1/csbasic/lab0

mkdir -p donphan3/{cleffa,bellossom}
touch donphan3/{gurdurr,mienfoo,drifblim}

mkdir -p krokorok4/{tentacruel,whismur}
touch krokorok4/mightyena

touch magneton2
touch roggenrola9
touch solosis5

mkdir -p sudowoodo1/{hitmontop,sceptile}
touch sudowoodo1/purugly

echo "Развитые способности  Iron Fist" > donphan3/gurdurr
echo -e "Ходы  Bounce Drain \nPunch Dual Chop Helping Hand Knock Off Low Kick Role Play Sleep Talk \nSnore" > donphan3/mienfoo
echo "weigth=33.1 height=47.0 atk=8 def=4" > donphan3/drifblim
echo -e "Тип \nпокемона  DARK NONE" > krokorok4/mightyena
echo "Тип диеты  Ergovore" > magneton2
echo -e "Тип \nдиеты  Terravore" > roggenrola9
echo -e "weigth=2.2 height=12.0 atk=3 \ndef=4" > solosis5
echo -e "Способности  Scratch Growl Hypnosis Faint Attack Fury \nSwipes Charm Assist Captivate Slash Swagger Body Slam Attract Hone \nClaws" > sudowoodo1/purugly

echo "1 Done!"

#2
chmod 573 donphan3
chmod u=r,g=,o=r donphan3/gurdurr
chmod 440 donphan3/mienfoo
chmod 373 donphan3/cleffa
chmod u=rwx,g=wx,o=rwx donphan3/bellossom
chmod 004 donphan3/drifblim
chmod 311 krokorok4/tentacruel
chmod 620 krokorok4/mightyena
chmod 577 krokorok4/whismur
chmod 363 krokorok4
chmod 440 magneton2
chmod u=r,g=,o=r roggenrola9
chmod 660 solosis5
chmod 500 sudowoodo1
chmod 624 sudowoodo1/purugly
chmod 512 sudowoodo1/hitmontop
chmod 537 sudowoodo1/sceptile

echo "2 Done!"

#3
chmod u=rwx,g=rw-,o=rw- ../lab0/*
chmod u=rwx,g=rw-,o=rw-  ../lab0/**/*

cp magneton2 sudowoodo1/puruglymagneton
cat donphan3/gurdurr donphan3/mienfoo > solosis5_67
ln roggenrola9 sudowoodo1/puruglyroggenrola
ln -s ../lab0/sudowoodo1 Copy_62
ln -s solosis5 donphan3/gurdurrsolosis
cp magneton2 sudowoodo1/sceptile
cp -RP ./sudowoodo1/* ./krokorok4/whismur/

echo "3 Done!"

#4
mkdir tmp
wc -l donphan3/gurdurr donphan3/mienfoo donphan3/drifblim | sort
ls -Rl 2>/dev/null | grep "m$" | sort -r
cat -n donphan3/gurdurr donphan3/mienfoo donphan3/drifblim 2>&1 | sort
ls -Rl 2>tmp/error.log | grep "ro" | tail -3 | sort
wc -l ./* 2>/dev/null | grep "^m" | sort
ls -Rl 2>/dev/null | grep "mi" | sort -k3

echo "4 Done!"

#5
rm -f solosis5
rm -f donphan3/drifblim
rm -f donphan3/gurdurrsolos*
rm -f sudowoodo1/puruglyroggenro*
rm -rf sudowoodo1
rmdir donphan3/cleffa

echo "5 Done!"