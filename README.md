### Run application
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

Download application: <b>TODO add link to place where it could be downloaded</b>

Run 

```bash
cd <path-to-app> && \
     pathToPropertiesFile=<path-to-property-file> \
     java -jar time-tracker-1.0-SNAPSHOT.jar
```

#### IDEA
Project contains IDEA's configuration for local run: <i>time_tracker.Launcher</i>.

### Configuration

Example of property file:
```yaml
stopwatch:
  dates:
    amountOfDaysToShow: 30
  devMode: true
  folderWithData: <replace-with-folder-where-you-want-to-store-data>
  defaultRecords:
    - record name 1
    - record name 2
    - record name 3
```