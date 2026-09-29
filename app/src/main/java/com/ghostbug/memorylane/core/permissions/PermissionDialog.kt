package com.ghostbug.memorylane.core.permissions

import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog (
    permission:String,
    isPermanentlyDeclined:Boolean,
    onDismiss:()->Unit ,
    onOkClick:()->Unit,
    onGoToAppSettingsClick:()->Unit,
    modifier: Modifier =Modifier
){
    AlertDialog(onDismissRequest = onDismiss,
        buttons = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ){
                HorizontalDivider()
                Text(
                    text = if (isPermanentlyDeclined){
                        "Grant permission "
                    } else {
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                    .fillMaxWidth()
                        .clickable{
                            if (isPermanentlyDeclined){
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )

            }
            },
        title ={
            Text(text = "Permission is required")

        },
        text = {


        },
        modifer = modifier

    )
}