//package com.expensetracker.data.db
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.expensetracker.data.db.dao.*
//import com.expensetracker.data.db.entities.*
//
//@Database(
//    entities = [User::class, Transaction::class, Budget::class, Wallet::class],
//    version = 1,
//    exportSchema = false
//)
//abstract class  AppDatabase : RoomDatabase() {
//
//    abstract fun userDao(): UserDao
//    abstract fun transactionDao(): TransactionDao
//    abstract fun budgetDao(): BudgetDao
//    abstract fun walletDao(): WalletDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getInstance(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "expense_tracker_db"
//                )
//                .fallbackToDestructiveMigration()
//                .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}

    package com.expensetracker.data.db

    import android.content.Context
    import androidx.room.Database
    import androidx.room.Room
    import androidx.room.RoomDatabase
    import com.expensetracker.data.db.dao.BudgetDao
    import com.expensetracker.data.db.dao.TransactionDao
    import com.expensetracker.data.db.dao.UserDao
    import com.expensetracker.data.db.dao.WalletDao
    import com.expensetracker.data.db.entities.Budget
    import com.expensetracker.data.db.entities.Transaction
    import com.expensetracker.data.db.entities.User
    import com.expensetracker.data.db.entities.Wallet

    @Database(
        entities = [User::class, Transaction::class, Budget::class, Wallet::class],
        version = 1,
        exportSchema = false
    )
    abstract class AppDatabase : RoomDatabase() {

        abstract fun userDao(): UserDao
        abstract fun transactionDao(): TransactionDao
        abstract fun budgetDao(): BudgetDao
        abstract fun walletDao(): WalletDao

        companion object {
            @Volatile
            private var INSTANCE: AppDatabase? = null

            fun getInstance(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "expense_tracker_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { INSTANCE = it }
                }
            }
        }
    }