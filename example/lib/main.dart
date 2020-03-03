import 'package:flutter/material.dart';
import 'package:handle_notification/handle_notification.dart';
import 'package:handle_notification_example/second_screen.dart';

import 'first_screen.dart';

//final RouteObserver<PageRoute<dynamic>> routeObserver =
//    RouteObserver<PageRoute<dynamic>>();

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorObservers: [HandleNotification.routeObserver],
      initialRoute: '/',
      routes: <String, WidgetBuilder>{
        "/": (BuildContext context) => FirstScreen(),
        "/second": (BuildContext context) => SecondScreen()
      },
      //navigatorObservers: [routeObserver],
    );
  }
}
