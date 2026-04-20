import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(const OmnibusClientApp());
}

class OmnibusClientApp extends StatelessWidget {
  const OmnibusClientApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'PTOB Flutter Client',
      theme: ThemeData(colorSchemeSeed: Colors.indigo, useMaterial3: true),
      home: const HealthCheckScreen(),
    );
  }
}

class HealthCheckScreen extends StatefulWidget {
  const HealthCheckScreen({super.key});

  @override
  State<HealthCheckScreen> createState() => _HealthCheckScreenState();
}

class _HealthCheckScreenState extends State<HealthCheckScreen> {
  final TextEditingController _baseUrlController =
      TextEditingController(text: 'http://localhost:8080');
  String _result = 'Tap "Check Health" to call the backend.';
  bool _loading = false;

  @override
  void dispose() {
    _baseUrlController.dispose();
    super.dispose();
  }

  Future<void> _checkHealth() async {
    setState(() {
      _loading = true;
      _result = 'Checking backend health...';
    });

    final baseUrl = _baseUrlController.text.trim();
    final uri = Uri.parse('$baseUrl/actuator/health');

    try {
      final response = await http.get(uri);
      final prettyBody = const JsonEncoder.withIndent('  ').convert(
        jsonDecode(response.body),
      );
      setState(() {
        _result = 'Status: ${response.statusCode}\n\n$prettyBody';
      });
    } catch (error) {
      setState(() {
        _result = 'Request failed: $error';
      });
    } finally {
      setState(() {
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Omnibus Health Checker')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(
              controller: _baseUrlController,
              decoration: const InputDecoration(
                labelText: 'Backend Base URL',
                hintText: 'http://localhost:8080',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            FilledButton(
              onPressed: _loading ? null : _checkHealth,
              child: Text(_loading ? 'Checking...' : 'Check Health'),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: SingleChildScrollView(
                child: SelectableText(_result),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
