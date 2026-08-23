Command to test:

```shell
adb shell am start \
  -a android.provider.action.REVIEW \
  -c android.intent.category.DEFAULT \
  -t image/jpeg \
  -d content://media/external/images/media/11062
```