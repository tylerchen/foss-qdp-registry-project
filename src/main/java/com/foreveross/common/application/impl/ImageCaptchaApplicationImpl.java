/*******************************************************************************
 * Copyright (c) Aug 11, 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
 ******************************************************************************/
package com.foreveross.common.application.impl;

import com.foreveross.common.application.ImageCaptchaApplication;
import com.jhlabs.image.PinchFilter;
import com.octo.captcha.CaptchaException;
import com.octo.captcha.CaptchaQuestionHelper;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.gimpy.DefaultGimpyEngine;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.inject.Named;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 验证码
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 11, 2016
 */
@Named("imageCaptchaApplication")
public class ImageCaptchaApplicationImpl implements ImageCaptchaApplication {
    private static final ImageCaptchaService instance = new DefaultManageableImageCaptchaService(
            new FastHashMapCaptchaStore(), new CSRCaptchaEngine(), 180, 100000, 75000);

    public byte[] getImageForID(String sessionId) {
        try {
            BufferedImage challenge = instance.getImageChallengeForID(sessionId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048);
            ImageIO.write(challenge, "png", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
        }
        return new byte[0];
    }

    public boolean validateForID(String sessionId, Object validate) {
        try {
            return instance.validateResponseForID(sessionId, validate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class CSRCaptchaEngine extends DefaultGimpyEngine {
        @Override
        protected void buildInitialFactories() {
            WordGenerator wordGenerator = new RandomWordGenerator(
                    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
            TextPaster randomPaster = new GlyphsPaster(4, 4,
                    new RandomRangeColorGenerator(new int[]{0, 100}, new int[]{0, 100}, new int[]{0, 100}),
                    new GlyphsVisitors[]{new TranslateGlyphsVerticalRandomVisitor(1.0D),
                            new OverlapGlyphsUsingShapeVisitor(1.0D), new TranslateAllToRandomPointVisitor()});
            BackgroundGenerator background = new GradientBackgroundGenerator(50, 25, Color.WHITE, Color.GRAY);
            FontGenerator font = new RandomFontGenerator(22, 24,
                    new Font[]{new Font("nyala", 1, 22), new Font("Bell MT", 0, 22), new Font("Credit valley", 1, 22),
                            new Font("Arial", 1, 22), new Font("Tahoma", 1, 22), new Font("Verdana", 1, 22)},
                    false);
            List<ImageDeformation> textDef = new ArrayList<ImageDeformation>();
            {
                PinchFilter pinch = new PinchFilter();

                pinch.setAmount(-0.5F);
                pinch.setRadius(7.0F);
                pinch.setAngle(0.19634955F);
                pinch.setCentreX(0.5F);
                pinch.setCentreY(-0.01F);
                pinch.setEdgeAction(1);

                PinchFilter pinch2 = new PinchFilter();
                pinch2.setAmount(-0.6F);
                pinch2.setRadius(7.0F);
                pinch2.setAngle(0.19634955F);
                pinch2.setCentreX(0.3F);
                pinch2.setCentreY(1.01F);
                pinch2.setEdgeAction(1);

                PinchFilter pinch3 = new PinchFilter();
                pinch3.setAmount(-0.6F);
                pinch3.setRadius(7.0F);
                pinch3.setAngle(0.19634955F);
                pinch3.setCentreX(0.8F);
                pinch3.setCentreY(-0.01F);
                pinch3.setEdgeAction(1);

                textDef.add(new ImageDeformationByBufferedImageOp(pinch));
                textDef.add(new ImageDeformationByBufferedImageOp(pinch2));
                textDef.add(new ImageDeformationByBufferedImageOp(pinch3));
            }
            WordToImage word2image = new DeformedComposedWordToImage(font, background, randomPaster,
                    new ArrayList<ImageDeformation>(), new ArrayList<ImageDeformation>(), textDef);
            addFactory(new MyGimpyFactory(wordGenerator, word2image));
        }
    }

    public static class MyGimpyFactory extends GimpyFactory {

        public MyGimpyFactory(WordGenerator generator, WordToImage word2image) {
            super(generator, word2image);
        }

        public ImageCaptcha getImageCaptcha(Locale locale) {
            Integer wordLength = getRandomLength();

            String word = getWordGenerator().getWord(wordLength, locale);

            BufferedImage image = null;
            try {
                image = getWordToImage().getImage(word);
            } catch (Throwable e) {
                throw new CaptchaException(e);
            }

            ImageCaptcha captcha = new MyGimpy(CaptchaQuestionHelper.getQuestion(locale, BUNDLE_QUESTION_KEY), image,
                    word, false);
            return captcha;
        }
    }

    @SuppressWarnings("serial")
    public static class MyGimpy extends ImageCaptcha implements Serializable {
        private String response;
        private boolean caseSensitive;

        public MyGimpy(String question, BufferedImage challenge, String response, boolean caseSensitive) {
            super(question, challenge);
            this.response = response;
            this.caseSensitive = caseSensitive;
        }

        public MyGimpy(String question, BufferedImage challenge, String response) {
            this(question, challenge, response, true);
        }

        public Boolean validateResponse(Object response) {
            return (null != response) && ((response instanceof String)) ? validateResponse((String) response)
                    : Boolean.FALSE;
        }

        private Boolean validateResponse(String response) {
            return Boolean
                    .valueOf(caseSensitive ? response.equals(this.response) : response.equalsIgnoreCase(this.response));
        }
    }
}
