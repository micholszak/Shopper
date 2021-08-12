package com.shopper.cache.internal

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.shopper.cache.internal.model.AddProductEntity
import com.shopper.cache.internal.model.CheckedProductEntity
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

internal class ProductDaoTest {

    private lateinit var database: ShopperDatabase
    private lateinit var productDao: ProductDao

    @Before
    fun before() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopperDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        productDao = database.productsDao()
    }

    @After
    @Throws(IOException::class)
    fun after() {
        database.close()
    }

    @Test
    fun productAddsProperly() = runBlocking {
        val product = AddProductEntity(name = "orange")
        productDao.addProduct(product)
        productDao.getAllProducts().test {
            val products = awaitItem()
            assertThat(products).hasSize(1)
            assertThat(products[0].name).isEqualTo("orange")
        }
    }

    @Test
    fun thereShouldBeNoDuplicatesInProducts() = runBlocking {
        repeat(5) {
            val orange = AddProductEntity(name = "orange")
            productDao.addProduct(entity = orange)
        }

        productDao.getAllProducts().test {
            val products = awaitItem()
            assertThat(products).doesNotHaveDuplicates()
        }
    }

    @Test
    fun daoShouldSendAnUpdateEveryTimeWhenProductIsAdded() = runBlocking {
        productDao.getAllProducts().test {
            productDao.addProduct(entity = AddProductEntity(name = "orange"))
            assertThat(awaitItem()).hasSize(1)
            productDao.addProduct(entity = AddProductEntity(name = "apple"))
            assertThat(awaitItem()).hasSize(2)
        }
    }

    @Test
    fun changeCheckedStateOfRequestedItem() = runBlocking {
        productDao.addProduct(entity = AddProductEntity(name = "orange"))
        productDao.getAllProducts().test {
            val orange = awaitItem().first()
            assertThat(orange.checked).isFalse
            productDao.updateChecked(
                entity = CheckedProductEntity(
                    productId = orange.productId,
                    checked = true
                )
            )
            val checkedOrange = awaitItem().first()
            assertThat(checkedOrange.checked).isTrue
        }
    }
}
