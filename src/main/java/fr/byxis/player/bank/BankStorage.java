package fr.byxis.player.bank;

import fr.byxis.fireland.Fireland;
import fr.byxis.storage.AbstractStorage;

public class BankStorage extends AbstractStorage
{
    private final BankAccount bankAccount;

    public BankStorage(Fireland main, String ownerId)
    {
        super(main, ownerId, "bank_storage", 1);
        this.bankAccount = new BankAccount(main, ownerId);
    }

    @Override
    public int getMaxSpaceStorage()
    {
        return bankAccount.getMaxSlots();
    }

    @Override
    public int getStorageSize(int storageNumber)
    {
        return getMaxSpaceStorage();
    }

    @Override
    public String getStorageTitle(int storageNumber)
    {
        return "§8Stockage de la banque";
    }
}
