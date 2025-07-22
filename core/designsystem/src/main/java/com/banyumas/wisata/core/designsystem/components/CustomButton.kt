package com.banyumas.wisata.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme

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
    enabled: Boolean = true,
    iconSize: Modifier = Modifier.size(24.dp),
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    isLoading: Boolean = false,
    loadingIndicator: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isCancel) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary,
            contentColor = if (isCancel) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary
        ),
        elevation = elevation,
        shape = RoundedCornerShape(8.dp),
        border = border,
        enabled = enabled && !isLoading
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement
        ) {
            if (isLoading) {
                loadingIndicator?.invoke()
            } else {
                Text(
                    text = text,
                    style = textStyle
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = iconContentDescription,
                        modifier = iconSize
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomButtonPreview() {
    WisataBanyumasTheme {
        CustomButton(
            text = "Navigasi ke Maps",
            onClick = {},
            iconContentDescription = "Navigate Icon",
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            iconSize = Modifier.size(20.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            isLoading = false,
            icon = Icons.Default.Add
        )
    }
}