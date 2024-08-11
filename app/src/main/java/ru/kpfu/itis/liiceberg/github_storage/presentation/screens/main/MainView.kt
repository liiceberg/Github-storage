package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.main

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kpfu.itis.liiceberg.github_storage.R
import ru.kpfu.itis.liiceberg.github_storage.data.model.GitHubAction
import ru.kpfu.itis.liiceberg.github_storage.domain.model.GitHubActionItem
import ru.kpfu.itis.liiceberg.github_storage.presentation.theme.GithubstorageTheme
import ru.kpfu.itis.liiceberg.github_storage.presentation.theme.JetTopAppBar
import ru.kpfu.itis.liiceberg.github_storage.util.formatDate
import java.time.LocalDate


@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
private fun MainViewPreview() {
    GithubstorageTheme {
        MainView("github.com/lldan", "path", LocalDate.now(), {}, {}, listOf(), true, false)
    }
}

@Composable
fun MainView(
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsStateWithLifecycle()

    MainView(
        repository = state.repository,
        folderPath = state.folderPath,
        accessActiveDate = state.accessDate,
        onPull = { viewModel.obtainEvent(MainScreenEvent.OnPullClicked) },
        onPush = { viewModel.obtainEvent(MainScreenEvent.OnPushClicked) },
        history = state.history,
        pullLoading = state.pullLoading,
        pushLoading = state.pushLoading
    )
}

@Composable
private fun MainView(
    repository: String,
    folderPath: String,
    accessActiveDate: LocalDate?,
    onPull: () -> Unit,
    onPush: () -> Unit,
    history: List<GitHubActionItem>,
    pullLoading: Boolean,
    pushLoading: Boolean,
) {
    Column(Modifier.fillMaxSize()) {
        JetTopAppBar(
            text = stringResource(id = R.string.main_page_title)
        )
        InfoContainer(
            repository,
            folderPath,
            accessActiveDate,
            onPull,
            onPush,
            pullLoading,
            pushLoading
        )
        Text(
            stringResource(id = R.string.main_page_last_history),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        HistoryList(list = history)
    }

}

@Composable
private fun InfoContainer(
    repository: String,
    folderPath: String,
    accessDate: LocalDate?,
    onPull: () -> Unit,
    onPush: () -> Unit,
    pullLoading: Boolean,
    pushLoading: Boolean,
) {
    Column {
        InfoContainerItem(
            title = stringResource(id = R.string.repository),
            text = repository
        )
        InfoContainerItem(
            title = stringResource(id = R.string.folder),
            text = folderPath
        )
        InfoContainerItem(
            title = stringResource(id = R.string.access),
            text = stringResource(
                id = R.string.main_page_access_text, accessDate?.formatDate() ?: ""
            ),
            access = accessDate?.isBefore(LocalDate.now())?.not()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.weight(1f)) {
                JetButton(
                    text = stringResource(id = R.string.push_btn),
                    icon = R.drawable.push,
                    onPush,
                    pushLoading
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(Modifier.weight(1f)) {
                JetButton(
                    text = stringResource(id = R.string.pull_btn),
                    icon = R.drawable.pull,
                    onPull,
                    pullLoading
                )
            }
        }
    }
}

@Composable
private fun InfoContainerItem(title: String, text: String, access: Boolean? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val textColor = when (access) {
            true -> MaterialTheme.colorScheme.primary
            false -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
        )
    }
}

@Composable
private fun JetButton(
    text: String,
    @DrawableRes icon: Int,
    onButtonClick: () -> Unit,
    isLoad: Boolean
) {
    val ctx = LocalContext.current
    val permissionDeniedText = stringResource(id = R.string.permission_denied)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (hasWriteStoragePermission(ctx)) {
            onButtonClick.invoke()
        } else {
            Toast.makeText(ctx, permissionDeniedText, Toast.LENGTH_SHORT).show()
        }
    }

    Button(
        onClick = {
            if (hasWriteStoragePermission(ctx)) {
                onButtonClick.invoke()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestPermission(ctx)
                } else {
                    permissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
                }

            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = isLoad.not()
    ) {
        if (isLoad) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(11.25.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(text = text)
    }
}

@Composable
fun HistoryList(list: List<GitHubActionItem>) {
    LazyColumn {
        items(list.size) {
            ActionHistoryItem(list[it])
        }
    }
}

@Composable
private fun ActionHistoryItem(item: GitHubActionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val icon = when (item.name) {
            GitHubAction.PULL -> R.drawable.pull
            GitHubAction.PUSH -> R.drawable.push
        }
        Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = Color.Unspecified
            )
        }

        Box(Modifier.weight(1f)) {
            Column {
                Text(
                    text = item.date.formatDate(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val description = when (item.name) {
                    GitHubAction.PULL -> R.string.git_pull
                    GitHubAction.PUSH -> R.string.git_commit
                }
                Text(
                    text = stringResource(id = description),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }

        val statisticString = buildAnnotatedString {
            withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                append("+${item.statistic.plusStr}/")
            }
            withStyle(SpanStyle(MaterialTheme.colorScheme.error)) {
                append("-${item.statistic.minusStr}")
            }
        }
        Text(
            statisticString,
            style = MaterialTheme.typography.labelSmall
        )
    }
    HorizontalDivider()
}


@SuppressLint("ObsoleteSdkInt")
private fun hasWriteStoragePermission(ctx: Context): Boolean {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Environment.isExternalStorageManager()
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> true
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            ContextCompat.checkSelfPermission(
                ctx,
                WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        else -> true
    }
}

@RequiresApi(Build.VERSION_CODES.R)
private fun requestPermission(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
    intent.addCategory("android.intent.category.DEFAULT")
    intent.data = Uri.parse(String.format("package:%s", context.packageName))
    context.startActivity(intent)
}