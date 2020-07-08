package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.PortfolioRepository;
import com.github.princesslana.greedorama.Transaction;
import com.github.princesslana.greedorama.UserRepository;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

import java.util.HashSet;

public class HistoryCommand {

    private static final int TXNS_PER_PAGE = 10;

    private final DiscordRequest request;

    private final PortfolioRepository portfolios;

    private final UserRepository users;

    public HistoryCommand(
            DiscordRequest request, PortfolioRepository portfolios, UserRepository users) {
        this.request = request;
        this.portfolios = portfolios;
        this.users = users;
    }

    @ParsedEntity
    private static class Options {
        @Flag(shortName = 'p', longName = "page", description = "Display page")
        public Integer page = 1;
    }

    @CommandHandler(commandName = "history")
    public DiscordResponse portfolio(Options options) {

        var page = options.page;

        var guildId = request.getDispatcher().guildFromEvent(request.getEvent());
        var userId = request.getDispatcher().identityFromEvent(request.getEvent());

        var user = users.get(guildId, userId);
        var portfolio = portfolios.get(user);

        var txns = portfolio.getTransactions();

        var totalPages = (txns.size() + TXNS_PER_PAGE - 1) / TXNS_PER_PAGE;

        Preconditions.checkArgument(options.page <= totalPages && options.page > 0,
                "Page number is not in range!");

        if (options.page > totalPages || options.page <= 0) {
            return Format.error("Page number out of range");
        }

        var pageText = String.format("Page %d of %d",
                options.page,
                totalPages);

        var txnList = new StringBuilder();

        var byWhen = Ordering.natural().reverse().onResultOf(Transaction::getWhen);

        var dates = new HashSet<String>();

        txns = byWhen.sortedCopy(txns);

        for (var i = (page - 1) * TXNS_PER_PAGE; i < page * TXNS_PER_PAGE; i++) {

            if (i >= txns.size()) {
                break;
            }

            var txn = txns.get(i);
            var date = Format.date(txn.getWhen());

            var line = String.format("%15s %s: %s %s %d shares of %s for %s @ %s per share",
                    dates.contains(date)? "" : date + ",",
                    Format.time(txn.getWhen()),
                    (txn.getQuantity() > 0) ? Emoji.BUY : Emoji.SELL,
                    (txn.getQuantity() > 0) ? "Bought" : "Sold",
                    Math.abs(txn.getQuantity()),
                    txn.getSymbol(),
                    Format.money(txn.getTotalPrice().abs()),
                    Format.money(txn.getUnitPrice()));

            txnList.append(line + "\n");
            dates.add(date);
        }

        return DiscordResponse.of(
                Joiner.on("\n").join("```", "Transactions", txnList.toString(), pageText, "```"));

    }
}
