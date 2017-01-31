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
		//ä�ù� ����� ��� ��ȯ�� ����Ʈ
		List<String> chatList = new ArrayList<>();
		
		//ä�ù渮��Ʈ�ʿ��� Ű������ �̾Ƴ��� ä�ù� �̸� ����� ��
		Iterator<String> chatListIt = chatListMap.keySet().iterator();
		while(chatListIt.hasNext()){
			chatList.add(chatListIt.next());
		}
		
		return chatList;
	}
	
	@Override
	public boolean makeNewChat(String chatName) throws RemoteException {
		//�ش� �̸��� ä�ù��� �̹� �ִ��� Ȯ���ϱ� ���� ä�ø���Ʈ���� Ű�¿� ���ԵǾ��ִ��� Ȯ��
		boolean isExist = chatListMap.keySet().contains(chatName.trim());
		//���ο� ä�ù��� ������ �����ߴ��� ���θ� ��ȯ�ϱ� ���� ����
		boolean result = false;
		
		if(isExist){//�̹� ���� �̸��� ä�ù��� ����
			result = false;
		}else{//���� �̸��� ä�ù��� �����Ƿ� ���� ������
			//���ο� ä�ù� ����Ʈ�� �����ϰ�
			ArrayList<ChatClientInf> chat = new ArrayList<ChatClientInf>();
			//ä�ø���Ʈ�ʿ� ä�ù� �̸����� ä�ø���Ʈ�� ����
			chatListMap.put(chatName.trim(),chat);
			//���ο� ä�ù� ������ �����Ͽ����Ƿ� ������� true�� ��ȯ��
			result = true;
		}
		
		return result;
	}
	
	@Override
	public void joinChat(String chatName, ChatClientInf client) throws RemoteException {
		//ä�ù濡 �����ϱ� ���� �ش� ä�ù� �̸��� ������ ����� ������
		ArrayList<ChatClientInf> chatList = chatListMap.get(chatName);
		//������ ��Ͽ� �߰�
		chatList.add(client);
		
	}
	
	@Override
	public void exitChat(String chatName, ChatClientInf client) throws RemoteException {
		//ä�ù濡�� ������ ���� �ش� ä�ù� �̸��� ������ ����� ������
		ArrayList<ChatClientInf> chatList = chatListMap.get(chatName);
		//������ ��Ͽ��� ����
		chatList.remove(client);
		
	}

	@Override
	public void setMessage(String chatName, String msg) throws RemoteException {
		//ä�ù濡�� �޽����� �����ϱ� ���� �ش� ä�ù� �̸��� ������ ����� ������
		ArrayList<ChatClientInf> chatList = chatListMap.get(chatName);
		//ä�ù濡 �ִ� �����ڵ鿡�� �޽����� ������
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
