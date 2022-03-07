a downloader for cryptocurrency historical date from binance.

this is very alfa-version of this programm. it support now only export of 5 minutes time frame.

to compile the programm you nee maven installed. run following commands:

```
~#@?  mvn clean compile
~#@?  mvn dependency:copy-dependencies
```

to run the programm you need MongoDB installed and running befor starting download data.

you can start your local MongoDB server with command

```
~#@?  mongod --directoryperdb --dbpath *<path/to/database/folder>*
```

or simply run *start-mongodb.ps1* script first.

now try tu run programm with *update-candles.bat*:

```
~#@?  .\update-candles.bat -?                                                                         ?  572ms ?  ? ?
Usage: <main class> [-?] [-ck] [-ex] [-up] -ti=<ticker>
-?, -h, --help     Display this Help Message
-ck            check and cleanup.
-ex            export csv values.
-ti=<ticker>   Ticker.
-up            update ticker.
```

e.x. update and export BTCUSDT data:

```
 ~#@?  .\update-candles.bat -ti btcusdt -up -ex  
```

when programm finished a file BTCUSDT5m.csv is stored in active directory. content are all 5m candles like this:

```
TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME
"BTCUSDT";"5m";"2022-01-05T00-00";"46159.91000000";"46159.91000000";"46067.68000000";"46126.75000000";"58.43329000"
"BTCUSDT";"5m";"2022-03-06T18-50";"38954.01000000";"39027.43000000";"38954.01000000";"39011.06000000";"51.07573000"
... and so on ...
```
