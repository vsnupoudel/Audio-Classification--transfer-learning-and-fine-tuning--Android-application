## The following steps were completed
- Finetuning the YAMNET model from tfhub using available dataset
- Exported tflite model
- Using tflite in Android( Java) to get predictions ( Flutter also attempted)

## TODO
- Expand the current application to split audio file into 5sec interval, convert them to Java consumable format, predict each of them
- Then output the time of 'positively identified' sound activity
- Alternatively, while the java issue gets resolved, working on getting resmapled data from a flask API, the application will call this flask api.
