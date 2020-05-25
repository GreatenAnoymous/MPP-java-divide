for ((i=1;i<=25;i++));

do
n=5
f="./test"
f+=$n
f+="_ex"
f+=$i
f+=".yaml"
sf="../scenarios/random-100-100-05-random-"
sf+=$i
sf+=".scen"
#echo $f
python ./generateYaml.py --maps "random-100-100-05.map" --scene $sf --numAgents $n --fi $f
exit
done
