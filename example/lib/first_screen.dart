import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:handle_notification/handle_notification.dart';
import 'package:handle_notification/plugin_mixin.dart';

class FirstScreen extends StatefulWidget {
  @override
  _FirstScreenState createState() => _FirstScreenState();
}

class _FirstScreenState extends State<FirstScreen> with PluginMixin {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            FlatButton(
              onPressed: () async {
                try {
                  final result =
                      await HandleNotification.instance.showNotification(
                    'Pratik',
                    'Hello',
                    'abc@gmail.com',
                  );
                  print(result);
                } catch (error) {
                  print(error.toString());
                }
              },
              child: Text('Press Here'),
            ),
            FlatButton(
              onPressed: () {
                Navigator.of(context).pushNamed('/second');
              },
              child: Text('Next Screen'),
            )
          ],
        ),
      ),
    );
  }

  Future<void> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case "onNotification":
        print("_handleMethod");
        navigate();
    }
  }

  void navigate() {
    Navigator.of(context).pushNamed('/second');
  }

  @override
  void onOpenFromNotification(data) {
    navigate();
  }
}
