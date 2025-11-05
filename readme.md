# Infinite Image Library

A Java library providing infinite-space image manipulation with quadtree-based tiling.

## Overview

**Infinite Image Library** provides a drop-in replacement for Java's `BufferedImage` that supports unlimited dimensions through dynamic memory allocation. Unlike traditional `BufferedImage`, which requires fixed dimensions and allocates memory upfront for the entire image, this library creates image tiles on-demand only where content exists.

### Key Features

- **Infinite Canvas**: No predefined size limits - draw anywhere in unbounded 2D space
- **Memory Efficient**: Only allocates tiles where content exists, not empty space
- **Standard API**: Implements `Graphics2D` and `WritableRaster` for seamless integration
- **Sparse Storage**: Ideal for sparse image data, avoiding wasteful memory allocation
- **Dynamic Growth**: Automatically expands to accommodate any coordinate range

## When to Use

### ✅ Ideal Use Cases

- **Large-scale canvas applications** - Infinite whiteboards, drawing apps, or zoom-able interfaces
- **Sparse image rendering** - Content scattered across large areas with mostly empty space
- **Memory-conscious applications** - When you need large logical canvas but limited physical memory
- **Procedurally generated content** - When final canvas size is unknown upfront
- **Custom coordinate systems** - Ideal when the canvas requires a custom coordinate system (for example, using the center as the origin or allowing negative coordinates)

### ❌ Not Ideal For

- **Fixed-size images** - Standard `BufferedImage` is faster and more efficient
- **Real-time performance critical** - Some overhead from quadtree traversal
- **Simple use cases** - If you don't need infinite dimensions, use `BufferedImage`

## Quick Start

### Installation

```xml
<dependency>
    <groupId>com.novospir</groupId>
    <artifactId>infinite-image-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Basic Usage

```java
import com.novospir.libraries.*;
import java.awt.*;

void example() {
    // Create an infinite canvas
    InfiniteBufferedImage canvas = new InfiniteBufferedImage();
    Graphics2D g = canvas.createGraphics();

    // Draw anywhere in infinite space
    g.setColor(Color.RED);
    g.fillRect(-5000, -3000, 1000, 500);

    g.setColor(Color.BLUE);
    g.fillOval(10000, 10000, 2000, 2000);

    // Export a specific region as BufferedImage
    Rectangle viewport = new Rectangle(0, 0, 500, 500);
    BufferedImage output = canvas.toBufferedImage(viewport);

    g.dispose();
}
```

### Direct Pixel Access

```java
import com.novospir.libraries.*;

void example() {
    InfiniteBufferedImage img = new InfiniteBufferedImage();

    // Direct pixel manipulation
    img.setRGB(100, 200, Color.RED.getRGB());
    int pixel = img.getRGB(100, 200);

    // Access raster for bulk operations
    InfiniteWritableRaster raster = (InfiniteWritableRaster) img.getRaster();
    int[] pixelData = raster.getPixel(100, 200, null);
}
```

### Using Abstract Interfaces

```java
import com.novospir.libraries.*;

void example() {
    // Use abstract interface for polymorphic code
    AbstractBufferedImage img = new InfiniteBufferedImage();

    // Or use standard BufferedImage adapter
    AbstractBufferedImage standard = new AbstractBufferedImage.BufferedImageAdapter(
            800, 600, BufferedImage.TYPE_INT_ARGB);

    // Both support the same API
    img.setRGB(100, 200, Color.RED.getRGB());
    standard.setRGB(100, 200, Color.RED.getRGB());
}
```

## Architecture

### How It Works

The library uses a **quadtree data structure** to organize infinite image space:

```
Root Node (e.g., 512x512)
├── Quadrant 0 (256x256)
│   ├── Tile (128x128) [BufferedImage with pixel data]
│   ├── Tile (128x128) [BufferedImage with pixel data]
│   └── ...
├── Quadrant 1 (256x256) [empty - not allocated]
├── Quadrant 2 (256x256) [empty - not allocated]
└── Quadrant 3 (256x256) [empty - not allocated]
```

- **Leaf nodes**: Contains size-configurable `BufferedImage` tiles with actual pixel data
- **Internal nodes**: Organize space hierarchically, contain references to child quadrants
- **Dynamic growth**: Tree automatically expands to accommodate coordinates outside current bounds
- **Lazy allocation**: Tiles are created only when needed

### Components

| Class                    | Purpose                                                                |
|--------------------------|------------------------------------------------------------------------|
| `InfiniteBufferedImage`  | Main image class, manages quadtree and provides BufferedImage-like API |
| `QuadGraphics2D`         | Graphics2D implementation that routes drawing to appropriate tiles     |
| `InfiniteWritableRaster` | Low-level pixel access implementation                                  |
| `QuadNode`               | Individual quadtree node managing a spatial region                     |
| `AbstractBufferedImage`  | Interface enabling polymorphic use with standard BufferedImage         |
| `AbstractWritableRaster` | Interface for raster operations                                        |
| `DataBufferDecoder`      | Optimized pixel data interpretation                                    |

## Performance

### Trade-offs

Compared to standard `BufferedImage`:

| Aspect                     | InfiniteImage               | BufferedImage         |
|----------------------------|-----------------------------|-----------------------|
| **Memory Usage**           | ⚠️ Higher overhead per tile | ✅ Direct allocation   |
| **Allocation Speed**       | ⚠️ Slower (tree traversal)  | ✅ Very fast           |
| **Sparse Content**         | ✅ Massive savings           | ❌ Wastes memory       |
| **Large Images**           | ✅ Only allocated regions    | ❌ Can't fit in memory |
| **Coordinate Flexibility** | ✅ Any coordinates           | ❌ Fixed dimensions    |

### Benchmarks

From test suite (`InfiniteBufferedImageTest`):

```
Random writes:          100,000 pixels
Canvas size:            10,000 × 10,000
Tiles allocated:        ~100 (only used regions)
Actual memory:          ~6.5 MB (vs 400 MB for BufferedImage)
Memory savings:         98.4% for sparse content
```

### Performance Characteristics

**O(log n)** complexity for pixel access via quadtree traversal
- Single pixel operations: ~100-500ns *needs review
- Batch operations: More efficient due to reduced per-pixel overhead
- Drawing operations: Overhead from creating per-tile Graphics2D contexts

**Optimization tips:**
- Batch pixel operations when possible
- Prefer larger drawing operations over many small ones
- Export regions only when needed
- Use appropriate tile sizes (configurable via `config.properties`)

## API Reference

### InfiniteBufferedImage

```java
// Constructors
InfiniteBufferedImage();
InfiniteBufferedImage(int x, int y);

// Core operations
int getRGB(int x, int y);
void setRGB(int x, int y, int rgb);
Graphics2D createGraphics();
AbstractWritableRaster getRaster();
BufferedImage toBufferedImage(Rectangle bounds);

// Information
Rectangle getLogicalBounds();
int getAllocatedLeafCount();
```

### QuadGraphics2D

Implements full `Graphics2D` interface including:
- Primitive drawing: lines, rectangles, ovals, arcs, polygons
- Text rendering with multiple overloads
- Image drawing with transforms
- Shape drawing with `draw()` and `fill()`
- Transformations: translate, rotate, scale, shear
- Clipping operations


```java
void example() {
    InfiniteBufferedImage img = new InfiniteBufferedImage();
    QuadGraphics2D g = (QuadGraphics2D) img.createGraphics();

    g.setColor(Color.RED);
    g.setStroke(new BasicStroke(5));
    g.drawLine(0, 0, 1000, 1000);

    g.rotate(Math.PI / 4);
    g.fillRect(-100, -100, 200, 200);

    g.dispose(); // Always dispose when done!
}
```


### InfiniteWritableRaster

```java
// Pixel operations
int[] getPixel(int x, int y, int[] array);
void setPixel(int x, int y, int[] array);

// Batch operations
int[] getPixels(int x, int y, int w, int h, int[] array);
void setPixels(int x, int y, int w, int h, int[] array);

// Sample operations
int getSample(int x, int y, int band);
void setSample(int x, int y, int band, int value);
```

## Limitations

### Current Limitations

- ⚠️ **Not thread-safe** - Synchronization required for multithreaded access
- ⚠️ **Some methods not implemented** - Several methods currently throw `UnsupportedOperationException`
- ⚠️ **Performance overhead** - 10-30% slower than BufferedImage for dense content
- ⚠️ **Memory overhead** - ~1KB overhead per tile (plus 65KB for 128x128 tile data)
- ⚠️ **No tile persistence** - Trees are not serializable
- ⚠️ **Limited format support** - Currently only TYPE_INT_ARGB

### Known Issues

- Text rendering may have slight pixel deviations due to anti-aliasing
- Some transformation combinations not fully tested
- Bounds computation on every access can be optimized
- No automatic garbage collection of empty tiles (call `dispose()`)

## Configuration

Create `config.properties` in your classpath:

```properties
# Tile size in pixels (power of 2 recommended)
tile.size=128

# Other configuration options
# (See ConfigLoader for available settings)
```

## Testing

The library includes comprehensive tests:

```bash
# Run all tests
mvn test

# Run specific test suites
mvn test -Dtest=InfiniteBufferedImageTest
mvn test -Dtest=PerformanceBenchmarkTests

# See interactive demo
mvn test -Dtest=InteractiveInfiniteImageDemo
```

### Test Coverage

- ✅ Pixel-level operations (setRGB, getRGB)
- ✅ Graphics2D drawing operations
- ✅ Raster pixel/sample access
- ✅ Bounds calculation
- ✅ Performance benchmarks
- ✅ Memory efficiency verification

### Future Work

- [ ] Thread-safe implementation
- [ ] Tile serialization/persistence
- [ ] Automatic empty tile cleanup
- [ ] Additional pixel format support
- [ ] Memory-mapped files for very large images

## Todos

#### QuadGraphics2D
* 3 functions unsupported
* Optimize all functions for performance
* Properly overload all function variants
* Refactor to eliminate duplicate code across functions
* Implement dispose() to correctly free allocated resources
* Optimize node handling:
* If a node represents a uniform color region, clip and store only that color
* Update all relevant functions to preserve this optimization
* Add unit tests for all transformation methods

#### InfiniteWritableRaster
* 9 functions unsupported (8 pending support of the above)
* Verify full data buffer compatibility (across all supported types)
* Move band calculation logic to InfiniteBufferedImage.getType()

## Contributors & Architecture Notes

**Core Architecture**
- Built for the Java **AWT Graphics2D** API
- Utilizes a **Quadtree** spatial data structure
- Employs the **Adapter** pattern for `BufferedImage` compatibility

**Contributors**
- [sdkkds2125](https://github.com/sdkkds2125): Testing and validation  
- [A-Berk](https://github.com/A-Berk): Core development and design
- Developed under [Novospir](https://www.novospir.com/) for the InfiniteImage project

## License
This project is licensed under the [MIT License](./LICENSE) — feel free to use, modify, and share it.  
Contributions are always appreciated!

---

**Note**: This library is designed for use cases where standard `BufferedImage` limitations are problematic. For typical fixed-size image scenarios, continue using `BufferedImage` for optimal performance.



