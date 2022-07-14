### Run application

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
  devMode: false
```