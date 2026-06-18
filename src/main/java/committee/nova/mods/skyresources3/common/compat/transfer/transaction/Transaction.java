package committee.nova.mods.skyresources3.common.compat.transfer.transaction;

public final class Transaction implements TransactionContext, AutoCloseable {
    private boolean committed;

    private Transaction() {
    }

    public static Transaction openRoot() {
        return new Transaction();
    }

    public void commit() {
        this.committed = true;
    }

    @Override
    public boolean isSimulation() {
        return false;
    }

    @Override
    public void close() {
        this.committed = false;
    }
}
