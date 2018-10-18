import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;
import model.Photo;
import model.PhotoSize;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static model.PhotoSize.LARGE;
import static model.PhotoSize.MEDIUM;
import static model.PhotoSize.SMALL;


public class PhotoCrawler {

    private static final Logger log = Logger.getLogger(PhotoCrawler.class.getName());

    private final PhotoDownloader photoDownloader;

    private final PhotoSerializer photoSerializer;

    private final PhotoProcessor photoProcessor;

    public PhotoCrawler() throws IOException {
        this.photoDownloader = new PhotoDownloader();
        this.photoSerializer = new PhotoSerializer("./photos");
        this.photoProcessor = new PhotoProcessor();
    }

    public void resetLibrary() throws IOException {
        photoSerializer.deleteLibraryContents();
    }

    public void downloadPhotoExamples() {
        try {
            Observable<Photo> downloadedExamples =
                    photoDownloader.getPhotoExamples().compose(this::processBySize);
            downloadedExamples.subscribe(photo -> photoSerializer.savePhoto(photo));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForQuery(String query) throws IOException {
        try {
            Observable<Photo> queriedExamples =
                    photoDownloader.searchForPhotos(query).compose(this::processBySize);
            queriedExamples.subscribe(photo -> photoSerializer.savePhoto(photo));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForMultipleQueries(List<String> queries) throws IOException {
        try {
            Observable<Photo> queriedPhotos =
                    photoDownloader.searchForPhotos(queries).compose(this::processBySize);
            queriedPhotos.subscribe(photo -> photoSerializer.savePhoto(photo));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public Observable<Photo> processPhotos(Observable<Photo> photos) {
        return photos
                .filter(photo -> photoProcessor.isPhotoValid(photo))
                .map(photo -> photoProcessor.convertToMiniature(photo));
    }

    public Observable<Photo> processBySize(Observable<Photo> photos) {
        return photos
                .filter(photo -> photoProcessor.isPhotoValid(photo))
                .groupBy(photo -> PhotoSize.resolve(photo))
                .flatMap(group -> {
                    if (group.getKey() == MEDIUM) {
                        return group
                                .buffer(5, 5, TimeUnit.SECONDS)
                                .flatMapIterable(photo -> photo);
                    } else {
                        return group.observeOn(Schedulers.computation());
                    }
                })
                .map(photo -> photoProcessor.convertToMiniature(photo));
    }
}
