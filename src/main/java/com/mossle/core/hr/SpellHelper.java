package com.mossle.core.hr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpellHelper {
    private static Logger logger = LoggerFactory.getLogger(SpellHelper.class);
    private String name;
    private String spell;
    private List<String> spellList = new ArrayList<String>();
    private List<SpellDTO> candidates = new ArrayList<SpellDTO>();

    public SpellHelper(String name) {
        this(name, null);
    }

    public SpellHelper(String name, String spell) {
        this.name = name.trim();
        this.spell = spell;
    }

    public void execute() {
        // 拼音
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] chars = name.toCharArray();

        for (char c : chars) {
            String s = Character.toString(c);
            SpellDTO spellDto = new SpellDTO(c);

            if (s.matches("[\u4E00-\u9FA5]+")) {
                Set<String> set = new HashSet<String>();

                try {
                    set.addAll(Arrays.asList(PinyinHelper
                            .toHanyuPinyinStringArray(c, format)));
                    spellDto.setList(new ArrayList<String>(set));
                } catch (BadHanyuPinyinOutputFormatCombination ex) {
                    logger.error(ex.getMessage(), ex);
                    spellDto.setList(Collections.singletonList(s));
                }
            } else {
                spellDto.setList(Collections.singletonList(s));
            }

            candidates.add(spellDto);
        }

        if (spell != null) {
            this.spellList = new ArrayList<String>(Arrays.asList(spell
                    .split(" ")));
        } else {
            this.spell = "";

            for (SpellDTO spellDto : candidates) {
                this.spell += (" " + spellDto.getFirst());
                this.spellList.add(spellDto.getFirst());
            }

            if (this.spell.length() > 0) {
                this.spell = this.spell.substring(1);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getSpell() {
        return spell;
    }

    public List<String> getSpellList() {
        return spellList;
    }

    public List<SpellDTO> getCandidates() {
        return this.candidates;
    }
}
