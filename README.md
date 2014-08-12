JSPL
========

Download: http://ci.thinkofdeath.co.uk/job/JSPL/

Build: `./gradlew clean shadowJar`

Example
--------

```javascript
var pluginDescription = {
    name: "Test",
    version: "0.1"
};

var TestPlugin = Java.extend(Plugin, {
    onEnable: function () {
        print("Hello world");
        $.on("player/AsyncPlayerChatEvent", function (e) {
            print(e.message);
        });
        $.config.set("testing", "hello world");
        $.saveConfig();
    }
});

var plugin = new TestPlugin();
```