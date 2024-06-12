import 'dart:io';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';

class UploadDownloadScreen extends StatefulWidget {
  @override
  _UploadDownloadScreenState createState() => _UploadDownloadScreenState();
}

class _UploadDownloadScreenState extends State<UploadDownloadScreen> {
  File? _image;
  TextEditingController _fileNameController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Upload and Download'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            ElevatedButton(
              onPressed: _pickImage,
              child: Text('Upload Image'),
            ),
            SizedBox(height: 20),
            if (_image != null) Image.file(_image!),
            SizedBox(height: 20),
            TextField(
              controller: _fileNameController,
              decoration: InputDecoration(
                labelText: 'Enter Filename',
              ),
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _downloadImage,
              child: Text('Download Image'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _pickImage() async {
    // Implement image picking logic using image_picker package
    try {
      XFile? pickedFile = await ImagePicker().pickImage(source: ImageSource.gallery);
      if (pickedFile != null) {
        File image = File(pickedFile.path);
        var request = http.MultipartRequest('POST', Uri.parse('http://192.168.1.8:8080/files/upload'));
        request.files.add(await http.MultipartFile.fromPath('file', image.path));
        var response = await request.send();
        if (response.statusCode == 200) {
          print('File uploaded successfully');
          setState(() {
            _image = image;
          });
        } else {
          print('Failed to upload file');
        }
      } else {
        print('No image selected');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> _downloadImage() async {
    String? filename = _fileNameController.text;
    if (filename != null && filename.isNotEmpty) {
      try {
        var response = await http.get(Uri.parse('http://192.168.1.8:8080/files/download/$filename'));
        if (response.statusCode == 200) {
          final String downloadsPath = (await getDownloadsDirectory())!.path;
          final File file = File('$downloadsPath/$filename');
          await file.writeAsBytes(response.bodyBytes);
          print('File downloaded successfully to: $downloadsPath');
        } else {
          print('Failed to download file');
        }
      } catch (e) {
        print('Error: $e');
      }
    } else {
      print('Please enter a filename');
    }
  }
}
