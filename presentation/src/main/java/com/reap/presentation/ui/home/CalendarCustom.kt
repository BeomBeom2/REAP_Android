package com.reap.presentation.ui.home

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.reap.presentation.common.Colors
import com.reap.presentation.ui.home.shared.displayText
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarCustom() {
    val daysOfWeek = remember { daysOfWeek() }
    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
    val selections = remember { mutableStateListOf<LocalDate>() }

    val monthState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    val weekState = rememberWeekCalendarState(
        startDate = startMonth.atStartOfMonth(),
        endDate = endMonth.atEndOfMonth(),
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = daysOfWeek.first(),
    )

    CalendarHeader(daysOfWeek = daysOfWeek)

    HorizontalCalendar(
        state = monthState,
        dayContent = { day ->
            val isSelectable = day.position == DayPosition.MonthDate
            Day(
                day.date,
                isSelected = isSelectable && selections.contains(day.date),
                isSelectable = isSelectable,
            ) { clicked ->
                if (selections.contains(clicked)) {
                    selections.remove(clicked)
                } else {
                    selections.add(clicked)
                }
            }
        },
    )
}



@Composable
private fun CalendarTitle(
    monthState: CalendarState,
    weekState: WeekCalendarState,
) {
    val visibleMonth = rememberFirstVisibleMonthAfterScroll(monthState)
    val visibleWeek = rememberFirstVisibleWeekAfterScroll(weekState)
    MonthAndWeekCalendarTitle(
        currentMonth = visibleMonth.yearMonth,
        monthState = monthState,
        weekState = weekState,
    )
}


@Composable
fun MonthAndWeekCalendarTitle(
    currentMonth: YearMonth,
    monthState: CalendarState,
    weekState: WeekCalendarState,
) {
    val coroutineScope = rememberCoroutineScope()
    SimpleCalendarTitle(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
        currentMonth = currentMonth,
        goToPrevious = {
            coroutineScope.launch {
                    val targetMonth = monthState.firstVisibleMonth.yearMonth.previous
                    monthState.animateScrollToMonth(targetMonth)
            }
        },

        goToNext = {
            Log.e("asd", "ㅁㄴㅇ")
            coroutineScope.launch {
                    val targetMonth = monthState.firstVisibleMonth.yearMonth.next
                    monthState.animateScrollToMonth(targetMonth)
            }
        },
    )
}

val YearMonth.next: YearMonth get() = this.plus(1, DateTimeUnit.MONTH)
val YearMonth.previous: YearMonth get() = this.minus(1, DateTimeUnit.MONTH)

@Composable
fun SimpleCalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    isHorizontal: Boolean = true,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationIcon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Previous",
            onClick = goToPrevious,
            isHorizontal = isHorizontal,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .testTag("MonthTitle"),
            text = currentMonth.displayText(),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
        CalendarNavigationIcon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Next",
            onClick = goToNext,
            isHorizontal = isHorizontal,
        )
    }
}

@Composable
private fun CalendarNavigationIcon(
    imageVector: ImageVector,
    contentDescription: String,
    isHorizontal: Boolean = true,
    onClick: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .clickable(role = Role.Button, onClick = onClick),
) {
    val rotation by animateFloatAsState(if (isHorizontal) 0f else 90f)
    Icon(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .align(Alignment.Center)
            .rotate(rotation),
        imageVector = imageVector,
        contentDescription = contentDescription,
    )
}

@Composable
fun CalendarHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.displayText(),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun Day(
    day: LocalDate,
    isSelected: Boolean,
    isSelectable: Boolean,
    onClick: (LocalDate) -> Unit,
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .padding(6.dp)
            .clip(CircleShape)
            .background(color = if (isSelected) Colors.example1Selection else Color.Transparent)
            .clickable(
                enabled = isSelectable,
                showRipple = !isSelected,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when {
            isSelected -> Color.White
            isSelectable -> Color.Unspecified
            else -> Colors.example4GrayPast
        }
        Text(
            text = day.dayOfMonth.toString(),
            color = textColor,
            fontSize = 14.sp,
        )
    }
}
