import 'dart:async';

import 'package:bloc/bloc.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key}) : super(key: key);

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return BlocProvider<GroupChatBloc>(
      create: (context) => GroupChatBloc(context),
      child: Scaffold(
          body: Stack(
        children: <Widget>[
          Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              GroupLoadedPage(),
            ],
          ),
          Align(
            alignment: Alignment.bottomCenter,
            child: ReplyBox(),
          ),
        ],
      )),
    );
  }
}

class GroupLoadedPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final groupChatBloc = BlocProvider.of<GroupChatBloc>(context);

    return Expanded(
      child: ListView.builder(
          itemBuilder: (BuildContext context, int index) {
            return Container(
              height: 50,
              margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              decoration: BoxDecoration(
                color: Color.fromARGB(255, 255, 255, 255),
                boxShadow: [
                  BoxShadow(
                    color: Color.fromARGB(128, 218, 218, 218),
                    blurRadius: 5,
                  ),
                ],
                border: Border.all(
                    color: Color(0xAE212121),
                    width: 1,
                    style: BorderStyle.solid),
                borderRadius: BorderRadius.all(Radius.circular(6)),
              ),
              child: Text(groupChatBloc.groupChats[index]),
            );
          },
          controller: groupChatBloc.groupChatController,
          itemCount: groupChatBloc.groupChats.length),
    );
  }
}

class GroupChatBloc extends Bloc<GroupChatEvent, GroupChatState> {
  ScrollController groupChatController;
  FocusNode replyTextFocus = FocusNode();
  TextEditingController replyTextController;
  List<String> groupChats = [];

  GroupChatBloc(BuildContext context) {
    groupChatController = ScrollController();
    replyTextController = TextEditingController();
    for (int i = 0; i < 100; i++) {
      groupChats.add("Group Message #$i");
    }
    showKeyboard();
  }

  @override
  Future<void> close() {
    groupChatController.dispose();
    replyTextFocus.dispose();
    replyTextController.dispose();
    return super.close();
  }

  @override
  GroupChatState get initialState => GroupChatDefaultState();

  @override
  Stream<GroupChatState> mapEventToState(GroupChatEvent event) async* {
    if (event != null) {
      yield GroupChatDefaultState();
    }
  }

  void showKeyboard() {
    Timer(Duration(seconds: 3), () {
      replyTextFocus.requestFocus(FocusNode());
      SystemChannels.textInput.invokeMethod('TextInput.show');
      showKeyboard();
    });
  }
}

abstract class GroupChatEvent {
  GroupChatEvent([List props = const []]) : super();
}

abstract class GroupChatState {
  GroupChatState([List props = const []]) : super();
}

class GroupChatDefaultState extends GroupChatState {
  @override
  List<Object> get props => null;
}

class ReplyBox extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // ignore: close_sinks
    final groupChatBloc = BlocProvider.of<GroupChatBloc>(context);

    return Container(
      margin: EdgeInsets.only(left: 16, top: 16, bottom: 12, right: 16),
      decoration: BoxDecoration(
        color: Color.fromARGB(255, 255, 255, 255),
        boxShadow: [
          BoxShadow(
            color: Color.fromARGB(128, 218, 218, 218),
            blurRadius: 5,
          ),
        ],
        border: Border.all(
            color: Color(0xAE212121), width: 1, style: BorderStyle.solid),
        borderRadius: BorderRadius.all(Radius.circular(6)),
      ),
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.all(Radius.circular(10)),
        ),
        padding: EdgeInsets.only(left: 12, top: 2, right: 8, bottom: 2),
        child: Row(crossAxisAlignment: CrossAxisAlignment.center, children: [
          Expanded(
            child: TextField(
              focusNode: groupChatBloc.replyTextFocus,
              controller: groupChatBloc.replyTextController,
              textCapitalization: TextCapitalization.sentences,
              minLines: 1,
              maxLines: 4,
              textInputAction: TextInputAction.newline,
              style: TextStyle(fontSize: 16, color: Color(0xFF212121)),
              decoration: InputDecoration(
                  border: InputBorder.none, hintText: 'Start a message...'),
            ),
          ),
          Container(
            height: 32,
            width: 76,
            child: FlatButton(
              onPressed: () {},
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30.0)),
              child: Text(
                "Send",
                style: TextStyle(fontSize: 16),
              ),
              textColor: Colors.white,
              color: Color(0xFF00A3AD),
            ),
          )
        ]),
      ),
    );
  }
}
