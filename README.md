downloader for historical data from binance
===

this is very alfa-version of this programm. it support now only export of 5 minutes time frame.

to compile and run the programm you need *maven* and *MongoDB* installed.

compile and install the programm:

```
$  mvn clean package
```

start MongoDB (or simply run *start-mongodb.bat* script.):

```
$  mongod --directoryperdb --dbpath <path/to/database/folder>
```

now try to run programm:

```
$  .\update-candles.bat -?
Usage: <main class> [-?] [-cl] [-ex] [-up] [-ds=<decimalSeparator>] -ti=<ticker>
  -?, -h, --help     display this help message.
      -cl            cleanup incomplete data.
      -ds=<decimalSeparator>
                     decimal separator for csv-values. default '.'
      -ex            export data to csv-file.
      -ti=<ticker>   ticker name, e.g. BTCUSDT.
      -up            update data for ticker.
```

e.x. update and export BTCUSDT data:

```
 $  .\update-candles.bat -ti btcusdt -up -ex  
```

when programm finished a file BTCUSDT5m.csv is stored in current directory. content are all 5m candles like this:

```
TICKER;FRAME;TIME;OPEN;HIGH;LOW;CLOSE;VOLUME
"BTCUSDT";"5m";"2022-01-05T00-00";"46159.91000000";"46159.91000000";"46067.68000000";"46126.75000000";"58.43329000"
... and so on ...
```
