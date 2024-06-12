import 'package:flutter/material.dart';
import 'screens/upload_download_screen.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Upload and Download Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: UploadDownloadScreen(), // Set the home screen to UploadDownloadScreen
    );
  }
}
