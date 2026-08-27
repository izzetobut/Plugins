package com.izzet.gorevler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GorevYoneticisi {

    private final GorevlerPlugin plugin;
    private final Map<UUID, OyuncuGorevVerisi> veriler = new HashMap<>();

    public GorevYoneticisi(GorevlerPlugin plugin) {
        this.plugin = plugin;
    }

    public OyuncuGorevVerisi getVeri(Player oyuncu) {
        return veriler.computeIfAbsent(oyuncu.getUniqueId(), k -> new OyuncuGorevVerisi());
    }

    public void gorevAl(Player oyuncu, GorevTuru tur) {
        OyuncuGorevVerisi veri = getVeri(oyuncu);
        if (veri.aktifMi(tur)) {
            oyuncu.sendMessage("§cBu gorev zaten aktif!");
            return;
        }
        veri.gorevAl(tur);
        oyuncu.sendMessage("§a" + tur.getIsim() + " gorevi alindi! §7(" + tur.getAciklama() + ")");
        oyuncu.closeInventory();
    }

    public void ilerlemeEkle(Player oyuncu, GorevTuru tur, int miktar) {
        OyuncuGorevVerisi veri = getVeri(oyuncu);
        if (!veri.aktifMi(tur)) return;

        veri.ilerlemeArtir(tur, miktar);

        if (veri.getIlerleme(tur) >= tur.getHedef()) {
            tamamla(oyuncu, tur);
        }
    }

    private void tamamla(Player oyuncu, GorevTuru tur) {
        OyuncuGorevVerisi veri = getVeri(oyuncu);
        veri.goreviBitir(tur);

        oyuncu.showTitle(Title.title(
                Component.text("§a§lGOREV TAMAMLANDI"),
                Component.text(tur.getRenk() + tur.getIsim())
        ));
        oyuncu.playSound(oyuncu.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

        Economy economy = plugin.getEconomy();
        economy.depositPlayer(oyuncu, tur.getOdul());

        oyuncu.sendMessage("§a" + tur.getIsim() + " gorevi tamamlandi! §e+" + tur.getOdul() + " para §7kazandin.");
        oyuncu.sendMessage("§7Tekrar almak icin §f/gorevler §7komutunu kullan.");
    }
}
