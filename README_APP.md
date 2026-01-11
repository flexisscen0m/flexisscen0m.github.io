# Daily Counter - Android Widget

Минимальное Android-приложение с виджетом для главного экрана.

## Описание

Виджет показывает:
- Иконку цветка
- Число посередине (счётчик)

## Функции

- **Клик по виджету** - увеличивает счётчик на 1
- **Автоматический сброс** - каждую полночь счётчик обнуляется

## Сборка

### Требования

- Android Studio Arctic Fox или новее
- Android SDK 34
- Minimum SDK: 26 (Android 8.0)

### Инструкции

1. Откройте проект в Android Studio
2. Синхронизируйте Gradle (File → Sync Project with Gradle Files)
3. Соберите проект (Build → Make Project)
4. Установите на устройство (Run → Run 'app')

### Командная строка

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Использование

1. После установки приложения
2. Долгое нажатие на главном экране
3. Добавить виджет → Daily Counter
4. Перетащите виджет на экран
5. Нажимайте на виджет для увеличения счётчика

## Структура проекта

```
app/
├── src/main/
│   ├── java/me/ianderdrache/dailycounter/
│   │   ├── CounterWidget.java       # Основной класс виджета
│   │   └── MidnightResetReceiver.java # Сброс в полночь
│   ├── res/
│   │   ├── drawable/
│   │   │   └── flower_icon.xml      # Векторная иконка цветка
│   │   ├── layout/
│   │   │   └── widget_layout.xml    # Layout виджета
│   │   ├── values/
│   │   │   └── strings.xml
│   │   └── xml/
│   │       └── widget_info.xml      # Метаданные виджета
│   └── AndroidManifest.xml
└── build.gradle
```

## Технические детали

- Счётчик хранится в SharedPreferences
- Проверка даты при каждом обновлении виджета
- AlarmManager для сброса в полночь
- Векторная графика для иконки
