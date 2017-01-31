package com.test.rmi.chat.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.test.rmi.chat.ChatClientInf;
import com.test.rmi.chat.ChatServerInf;

public class ChatServer extends UnicastRemoteObject implements ChatServerInf{

	private TreeMap<String, ArrayList<ChatClientInf>> chatListMap;
	private static Registry registry;

	public ChatServer() throws RemoteException{
		chatListMap = new TreeMap<String, ArrayList<ChatClientInf>>();
	}
	
	@Override
	public List<String> getChatList() throws RemoteException {
		//채팅방 목록을 담아 반환할 리스트
		List<String> chatList = new ArrayList<>();
		
		//채팅방리스트맵에서 키값들을 뽑아내면 채팅방 이름 목록이 됨
		Iterator<String> chatListIt = chatListMap.keySet().iterator();
		while(chatListIt.hasNext()){
			chatList.add(chatListIt.next());
		}
		
		return chatList;
	}
	
	@Override
	public boolean makeNewChat(String chatName) throws RemoteException {
		//해당 이름의 채팅방이 이미 있는지 확인하기 위해 채팅리스트맵의 키셋에 포함되어있는지 확인
		boolean isExist = chatListMap.keySet().contains(chatName.trim());
		//새로운 채팅방의 생성에 성공했는지 여부를 반환하기 위한 변수
		boolean result = false;
		
		if(isExist){//이미 같은 이름의 채팅방이 있음
			result = false;
		}else{//같은 이름의 채팅방이 없으므로 새로 생성함
			//새로운 채팅방 리스트를 생성하고
			ArrayList<ChatClientInf> chat = new ArrayList<ChatClientInf>();
			//채팅리스트맵에 채팅방 이름으로 채팅리스트를 넣음
			chatListMap.put(chatName.trim(),chat);
			//새로운 채팅방 생성에 성공하였으므로 결과값은 true를 반환함
			result = true;
		}
		
		return result;
	}
	
	@Override
	public void joinChat(String chatName, ChatClientInf client) throws RemoteException {
		//채팅방에 참가하기 위해 해당 채팅방 이름의 접속자 목록을 가져옴
		ArrayList<ChatClientInf> chatList = chatListMap.get(chatName);
		//접속자 목록에 추가
		chatList.add(client);
		
	}
	
	@Override
	public void exitChat(String chatName, ChatClientInf client) throws RemoteException {
		//채팅방에서 나가기 위해 해당 채팅방 이름의 접속자 목록을 가져옴
		ArrayList<ChatClientInf> chatList = chatListMap.get(chatName);
		//접속자 목록에서 삭제
		chatList.remove(client);
		
	}

	@Override
	public void setMessage(String chatName, String msg) throws RemoteException {
		//채팅방에서 메시지를 전송하기 위해 해당 채팅방 이름의 접속자 목록을 가져옴
		ArrayList<ChatClientInf> chatList = chatListMap.get(chatName);
		//채팅방에 있는 접속자들에게 메시지를 전송함
		for(ChatClientInf client:chatList){
			client.setMessage(msg);
		}
	}
	
	public static void main(String[] args) {
		try {
			registry = java.rmi.registry.LocateRegistry.createRegistry(1099);
			ChatServerInf server = new ChatServer();
			Naming.rebind("rmi://127.0.0.1/chat", server);
			System.out.println("Server Ready");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
}
