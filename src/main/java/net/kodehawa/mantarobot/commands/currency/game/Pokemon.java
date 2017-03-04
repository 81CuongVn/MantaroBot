package net.kodehawa.mantarobot.commands.currency.game;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.kodehawa.mantarobot.commands.currency.entity.player.EntityPlayer;
import net.kodehawa.mantarobot.commands.currency.game.core.Game;
import net.kodehawa.mantarobot.commands.currency.game.core.GameReference;
import net.kodehawa.mantarobot.commands.currency.world.TextChannelWorld;
import net.kodehawa.mantarobot.data.MantaroData;
import net.kodehawa.mantarobot.utils.commands.EmoteReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static net.kodehawa.mantarobot.utils.Utils.toByteArray;

public class Pokemon extends Game {

	private String expectedAnswer;
	private int maxAttempts = 10;
	private int attempts = 0;
	private static final Logger LOGGER = LoggerFactory.getLogger("Game[PokemonTrivia]");

	@Override
	public boolean onStart(GuildMessageReceivedEvent event, GameReference type, EntityPlayer player) {
		try{
			player.setCurrentGame(type, event.getChannel());
			TextChannelWorld.of(event.getChannel()).addEntity(player, type);
			Random rand = new Random();
			List<String> guesses = MantaroData.getPokemonGuesses().get();
			String[] data = guesses.get(rand.nextInt(guesses.size())).split("`");
			String pokemonImage = data[0];
			expectedAnswer = data[1];
			byte[] image = toByteArray(pokemonImage);

			if(image == null){
				onError(LOGGER, event, player, null);
				return false;
			}

			event.getChannel().sendFile(image, "pokemon.jpg",
					new MessageBuilder().append(EmoteReference.TALKING).append("Who's that pokemon?. You have 10 attempts to do it. (Type end to end the game)")
							.build()).queue();

			return true;
		} catch (Exception e){
			onError(LOGGER, event, player, e);
			return false;
		}
	}

	@Override
	public void call(GuildMessageReceivedEvent event, EntityPlayer player) {
		if (!(EntityPlayer.getPlayer(event.getAuthor().getId()).getId() == player.getId() && player.getGame() == GameReference.TRIVIA
				&& !event.getMessage().getContent().startsWith(MantaroData.getData().get().getPrefix(event.getGuild())))) {
			return;
		}

		if(event.getMessage().getContent().equalsIgnoreCase("end")){
			endGame(event, player, false);
			return;
		}

		if (attempts > maxAttempts) {
			event.getChannel().sendMessage(EmoteReference.SAD + "You used all of your attempts, game is ending.").queue();
			endGame(event, player, false);
			return;
		}

		if(event.getMessage().getContent().equalsIgnoreCase(expectedAnswer)){
			onSuccess(player, event);
			return;
		}

		event.getChannel().sendMessage(EmoteReference.SAD + "That wasn't it! "
				+ EmoteReference.STOPWATCH + "You have " + (maxAttempts - attempts) + " attempts remaning").queue();

		attempts++;
	}
}
