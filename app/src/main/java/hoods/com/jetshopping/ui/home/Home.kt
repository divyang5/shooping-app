package hoods.com.jetshopping.ui.home

import android.graphics.drawable.shapes.Shape
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hoods.com.jetshopping.data.room.ItemsWithStoreAndList
import hoods.com.jetshopping.data.room.models.Item
import hoods.com.jetshopping.ui.Category
import hoods.com.jetshopping.ui.Utils
import hoods.com.jetshopping.ui.theme.Shapes
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun HomeScreen(
    onNavigate:(Int) -> Unit
) {
    val homeViewModel= viewModel(modelClass = HomeViewModel::class.java)
    val homeState=homeViewModel.state
    Scaffold(
        floatingActionButton= {
            FloatingActionButton(onClick = {onNavigate.invoke(-1)}) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) {paddingValues->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues.calculateBottomPadding())
        ) {
            item {
                LazyRow {
                    items(Utils.category){category : Category ->
                        CategoryItem(iconRes = category.resId,
                            title = category.title,
                            selected = category == homeState.category
                        ) {
                            homeViewModel.onCategoryChange(category)
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                }
            }
            items(homeState.items){
                ShoppingItems(
                    item = it,
                    isChecked = it.item.isChecked,
                    onCheckedChange = homeViewModel::onItemCheckedChange
                ) {
                    onNavigate.invoke(it.item.id)
                }
            }
        }
    }
}


@Composable
fun CategoryItem(
    @DrawableRes iconRes:Int,
    title:String,
    selected:Boolean,
    onItemClick:()->Unit
) {

    Card(
       modifier = Modifier
           .padding(top = 6.dp, bottom = 6.dp, start = 6.dp)
           .selectable(
               selected = selected,
               interactionSource = remember { MutableInteractionSource() },
               indication = rememberRipple(
                   bounded = true,
                   radius = 100.dp,
                   color = MaterialTheme.colors.onPrimary
               ),
               onClick = { onItemClick.invoke() }
           ),
        border = BorderStroke(1.dp,
            if(selected) MaterialTheme.colors.primary.copy(.5f)
            else MaterialTheme.colors.onSurface
            ),
        shape = Shapes.large,
        backgroundColor = if(selected) MaterialTheme.colors.primary.copy(.5f)
        else MaterialTheme.colors.surface,
        contentColor = if(selected) MaterialTheme.colors.onPrimary
        else MaterialTheme.colors.onSurface
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Icon(painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Normal,

            )

        }

    }
}

@Composable
fun ShoppingItems(
    item: ItemsWithStoreAndList,
    isChecked:Boolean,
    onCheckedChange:(Item,Boolean) ->Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick.invoke()
            }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){

            Column(
                modifier = Modifier.padding(8.dp)
            ) {

                Text(
                    text = item.item.itemName,
                    style=MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = item.store.storeName
                )
                Spacer(modifier = Modifier.padding(4.dp))
                CompositionLocalProvider(
                    LocalContentAlpha provides
                        ContentAlpha.disabled
                ) {
                    Text(
                        text = hoods.com.jetshopping.ui.home.FormateDate(item.item.date),
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Qty : ${item.item.qty}",
                    style=MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Checkbox(
                    checked = isChecked,
                    onCheckedChange ={
                        onCheckedChange.invoke(item.item,it)
                    }
                )
            }
        }

    }

}

fun FormateDate(date: Date):String =
    SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(date)