package me.andrewdisco.rektanticheat.playerInformation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.entity.Player;

public class CityCheck {

	public static String getCity(Player player) throws Exception{


		final URL url = new URL("https://ip-api.io/json/" + player.getAddress().toString().replaceAll("/", ""));
		final BufferedReader stream = new BufferedReader(new InputStreamReader(
				url.openStream()));
		final StringBuilder entirePage = new StringBuilder();
		String inputLine;
		while ((inputLine = stream.readLine()) != null) {
			entirePage.append(inputLine);
		}
		stream.close();
		if(entirePage.toString().contains("\"city\":\"")){

			return "Unknown";
		}else{

		}
		return entirePage.toString().split("\"city\":\"")[1].split("\",")[0];




	}

	public static String getTimeZone(Player player) throws Exception{


		final URL url = new URL("https://ip-api.io/json/" + player.getAddress().toString().replaceAll("/", ""));
		final BufferedReader stream = new BufferedReader(new InputStreamReader(
				url.openStream()));
		final StringBuilder entirePage = new StringBuilder();
		String inputLine;
		while ((inputLine = stream.readLine()) != null) {
			entirePage.append(inputLine);
		}
		stream.close();
		if(entirePage.toString().contains("\"time_zone\":\"")){

			return "Unknown";
		}else{

		}
		return entirePage.toString().split("\"time_zone\":\"")[1].split("\",")[0];




	}




}

