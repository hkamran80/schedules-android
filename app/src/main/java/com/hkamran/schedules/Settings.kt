package com.hkamran.schedules

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hkamran.schedules.ui.theme.SchedulesTheme

@Composable
fun Settings() {

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.settings_label), style = MaterialTheme.typography.h3)
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column {
            SettingsCard {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(stringResource(R.string.theme_label))
                    Text(
                        "Hi"
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        modifier = modifier.fillMaxWidth(),
        content = content
    )
}

@Preview(name = "Settings (Light)", showBackground = true, group = "Settings")
@Composable
fun SettingsPreview() {
    SchedulesTheme {
        Settings()
    }
}