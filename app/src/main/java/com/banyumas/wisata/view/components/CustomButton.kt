package com.banyumas.wisata.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.view.theme.AppTheme

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconContentDescription: String? = null,
    isCancel: Boolean = false,
    elevation: ButtonElevation = ButtonDefaults.buttonElevation(2.dp),
    border: BorderStroke? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isCancel) AppTheme.colorScheme.background else AppTheme.colorScheme.primary,
            contentColor = if (isCancel) AppTheme.colorScheme.onBackground else AppTheme.colorScheme.onPrimary
        ),
        elevation = elevation,
        shape = AppTheme.shape.button,
        border = border,
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    modifier = Modifier.size(AppTheme.size.large)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = AppTheme.typography.labelLarge
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CustomButtonPreview() {
    AppTheme {
        CustomButton(
            text = "Navigasi ke Maps",
            onClick = {},
            iconContentDescription = "Logout",
            modifier = Modifier.padding(8.dp),
            border = null,
        )
    }
}
