import java.io.File
import java.lang.System.err
import java.time.ZoneId
import java.util.*

fun main(args: Array<String>) {
  val args = Args.parse(args)

  val parser = TimelineKmlParser()
  val tracks = ArrayList<Track>(1000)

  args.kmlDir.listFiles().filter { it.name.endsWith(".kml") || it.name.endsWith(".xml") }.forEach {
    try {
      tracks += parser.parse(it)
    }
    catch (e: Exception) {
      err.println("Failed to parse $it: $e")
    }
  }

  tracks.sortBy { it.startAt }

  tracks.forEach {
    println(it)
  }
  println(tracks.size)

  args.imageDir.walkTopDown().filter { it.isFile }.forEach { file ->
    val imageData = ImageData(file, args.timeZone)
    // TODO: skip already geotagged images
    println("$file ${imageData.dateTime}")
  }
}

data class Args(val kmlDir: File, val imageDir: File, val timeZone: ZoneId) {
  companion object {
    fun parse(args: Array<String>): Args {
      if (args.size < 3) {
        err.println("Usage: <kml-dir> <image-dir> <time-zone>")
        err.println("Local time-zone is: ${ZoneId.systemDefault()}, provide the one where the images were taken")
        System.exit(1)
      }
      return Args(File(args[0]), File(args[1]), ZoneId.of(args[2]))
    }
  }
}
