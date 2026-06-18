package committee.nova.mods.skyresources3.common.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

final class ItemColorCodecs {
    static final Codec<Integer> RGB_COLOR = Codec.STRING.comapFlatMap(
            ItemColorCodecs::parseRgb,
            color -> String.format("#%06X", color & 0xFFFFFF)
    );

    private static DataResult<Integer> parseRgb(final String value) {
        final String hex = value.startsWith("#") ? value.substring(1) : value;
        if (hex.length() != 6) {
            return DataResult.error(() -> "Expected RGB color in #RRGGBB format: " + value);
        }
        try {
            return DataResult.success(Integer.parseUnsignedInt(hex, 16));
        } catch (final NumberFormatException exception) {
            return DataResult.error(() -> "Invalid RGB color: " + value);
        }
    }

    private ItemColorCodecs() {
    }
}
