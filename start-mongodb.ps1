
if(-not(test-path data)){
    mkdir data
}

$MONOGOEXE="$env:MONGODB_HOME\bin\mongod.exe"
if(-not(Test-Path $MONOGOEXE) ){
    $MONOGOEXE="mongod.exe"
}
& $MONOGOEXE --directoryperdb --dbpath data
